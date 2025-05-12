package com.cs490.group4.controller.authentication;

import com.cs490.group4.constants.TokenConstants;
import com.cs490.group4.dto.authentication.*;
import com.cs490.group4.security.User;
import com.cs490.group4.security.config.JWTService;
import com.cs490.group4.service.authentication.AuthenticationService;
import com.cs490.group4.service.authentication.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.experimental.FieldNameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthenticationController {

    private static Dotenv dotenv = Dotenv.load();

    @FieldNameConstants.Exclude
    private static final String ENVIRONMENT_TYPE = dotenv.get("ENVIRONMENT_TYPE");

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
            AuthenticationResponseDTO responseDTO = authenticationService.register(request);

            //TODO: does cookie max age matter? I have a USER and ADMIN expiration, just using USER all the time
            if (responseDTO != null) {
                ResponseCookie accessTokenCookie, refreshTokenCookie;
                if (!ENVIRONMENT_TYPE.equals("localhost")) {
                    accessTokenCookie = ResponseCookie.from("access_token", responseDTO.getAccessToken())
                            .httpOnly(true).secure(true).sameSite("None").path("/").domain(".bryceblankinship.com").maxAge(TokenConstants.USER_ACCESS_TOKEN_EXPIRATION).build();

                    refreshTokenCookie = ResponseCookie.from("refresh_token", responseDTO.getRefreshToken())
                            .httpOnly(true).secure(true).sameSite("None").path("/").domain(".bryceblankinship.com").maxAge(TokenConstants.USER_REFRESH_TOKEN_EXPIRATION).build();
                } else {
                    accessTokenCookie = ResponseCookie.from("access_token", responseDTO.getAccessToken())
                            .httpOnly(false).secure(false).sameSite("Lax").path("/").domain(".localhost").maxAge(TokenConstants.USER_ACCESS_TOKEN_EXPIRATION).build();

                    refreshTokenCookie = ResponseCookie.from("refresh_token", responseDTO.getRefreshToken())
                            .httpOnly(false).secure(false).sameSite("Lax").path("/").domain(".localhost").maxAge(TokenConstants.USER_REFRESH_TOKEN_EXPIRATION).build();
                }
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(responseDTO);
            }
            return ResponseEntity.accepted().body("Email already exists in the database.");
    }

    @PostMapping("/auth/checkCredentials")
    public ResponseEntity<?> checkCredentials(@RequestBody AuthenticationRequestDTO request) {
        CredentialsCheckResponseDTO responseDTO = authenticationService.checkCredentials(request);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/auth/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO request) throws Exception {
            try {
                AuthenticationResponseDTO responseDTO = authenticationService.authenticate(request);

                ResponseCookie accessTokenCookie, refreshTokenCookie;
                if (!ENVIRONMENT_TYPE.equals("localhost")) {
                    accessTokenCookie = ResponseCookie.from("access_token", responseDTO.getAccessToken())
                            .httpOnly(true).secure(true).sameSite("None").path("/").domain(".bryceblankinship.com").maxAge(TokenConstants.USER_ACCESS_TOKEN_EXPIRATION).build();

                    refreshTokenCookie = ResponseCookie.from("refresh_token", responseDTO.getRefreshToken())
                            .httpOnly(true).secure(true).sameSite("None").path("/").domain(".bryceblankinship.com").maxAge(TokenConstants.USER_REFRESH_TOKEN_EXPIRATION).build();
                } else {
                    accessTokenCookie = ResponseCookie.from("access_token", responseDTO.getAccessToken())
                            .httpOnly(false).secure(false).sameSite("Lax").path("/").domain(".localhost").maxAge(TokenConstants.USER_ACCESS_TOKEN_EXPIRATION).build();

                    refreshTokenCookie = ResponseCookie.from("refresh_token", responseDTO.getRefreshToken())
                            .httpOnly(false).secure(false).sameSite("Lax").path("/").domain(".localhost").maxAge(TokenConstants.USER_REFRESH_TOKEN_EXPIRATION).build();
                }

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(responseDTO);
            } catch (Exception e) {
                return ResponseEntity.accepted().body(e);
            }
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@CookieValue("access_token") String accessToken) {
        User user = new User();
        try {
            Integer id = userService.getUserId(accessToken);
            user.setUserId(id);
            authenticationService.revokeAllUserTokens(user);

            ResponseCookie accessTokenCookie, refreshTokenCookie;
            if (!ENVIRONMENT_TYPE.equals("localhost")) {
                accessTokenCookie = ResponseCookie.from("access_token", "deleted")
                        .httpOnly(true).secure(true).sameSite("None").path("/").domain(".bryceblankinship.com").maxAge(TokenConstants.USER_ACCESS_TOKEN_EXPIRATION).build();

                refreshTokenCookie = ResponseCookie.from("refresh_token", "deleted")
                        .httpOnly(true).secure(true).sameSite("None").path("/").domain(".bryceblankinship.com").maxAge(TokenConstants.USER_REFRESH_TOKEN_EXPIRATION).build();
            } else {
                accessTokenCookie = ResponseCookie.from("access_token", "deleted")
                        .httpOnly(false).secure(false).sameSite("Lax").path("/").domain(".localhost").maxAge(TokenConstants.USER_ACCESS_TOKEN_EXPIRATION).build();

                refreshTokenCookie = ResponseCookie.from("refresh_token", "deleted")
                        .httpOnly(false).secure(false).sameSite("Lax").path("/").domain(".localhost").maxAge(TokenConstants.USER_REFRESH_TOKEN_EXPIRATION).build();
            }

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body("Successfully unauthenticated.");
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Something bad happened while trying to logout.");
        }

    }


    @PutMapping("/auth/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO dto){
        if(userService.isValidResetToken(dto.getEmail(), dto.getToken())){
            return ResponseEntity.ok(authenticationService.resetPassword(dto.getEmail(), dto.getPassword()));
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/auth/verifyJWTExpiration")
    public ResponseEntity<?> verifyTokenExpiration(@CookieValue("access_token") String accessToken, @CookieValue("refresh_token") String refreshToken) {
            try {
                if (!jwtService.isTokenExpired(accessToken)) {
                    return ResponseEntity.ok("Not Expired");
                }
            } catch (ExpiredJwtException e) {
                System.out.println("Token refresh requested");
                if (!jwtService.isTokenExpired(refreshToken)) {
                    System.out.println("Token refresh initiated");
                    AuthenticationResponseDTO responseDTO = authenticationService.refreshAccessToken(refreshToken);

                    ResponseCookie accessTokenCookie, refreshTokenCookie;
                    if (!ENVIRONMENT_TYPE.equals("localhost")) {
                        accessTokenCookie = ResponseCookie.from("access_token", responseDTO.getAccessToken())
                                .httpOnly(true).secure(true).sameSite("None").path("/").domain(".bryceblankinship.com").maxAge(TokenConstants.USER_ACCESS_TOKEN_EXPIRATION).build();

                        refreshTokenCookie = ResponseCookie.from("refresh_token", responseDTO.getRefreshToken())
                                .httpOnly(true).secure(true).sameSite("None").path("/").domain(".bryceblankinship.com").maxAge(TokenConstants.USER_REFRESH_TOKEN_EXPIRATION).build();
                    } else {
                        accessTokenCookie = ResponseCookie.from("access_token", responseDTO.getAccessToken())
                                .httpOnly(false).secure(false).sameSite("Lax").path("/").domain(".localhost").maxAge(TokenConstants.USER_ACCESS_TOKEN_EXPIRATION).build();

                        refreshTokenCookie = ResponseCookie.from("refresh_token", responseDTO.getRefreshToken())
                                .httpOnly(false).secure(false).sameSite("Lax").path("/").domain(".localhost").maxAge(TokenConstants.USER_REFRESH_TOKEN_EXPIRATION).build();
                    }

                    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString()).header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(responseDTO);
                }
                return ResponseEntity.accepted().body("Expired");
            }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

}
