package com.cs490.group4.service.authentication;

import com.cs490.group4.dto.authentication.*;
import com.cs490.group4.security.User;
import com.cs490.group4.security.UserRepository;
import com.cs490.group4.security.config.JWTService;
import com.cs490.group4.security.config.token.Token;
import com.cs490.group4.security.config.token.TokenRepository;
import com.cs490.group4.security.config.token.TokenType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public User resetPassword(String email, String newPassword) {
        User user = repository.findByEmail(email).orElseThrow();

        user.setPassword(passwordEncoder.encode(newPassword));

        return repository.save(user);
    }

    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole()).build();
//                .role(Role.USER).build();
        if (repository.findByEmail(user.getEmail()).isEmpty()) {
            User savedUser = repository.save(user);
            /*
              user successfully registered, so generate a token and send it back to the client
              if the user's role is an admin, this token will be short-lived
              if it's a normal user, it will live much longer
             */
            String accessToken = jwtService.generateTokens(user).get("access_token");
            String refreshToken = jwtService.generateTokens(user).get("refresh_token");

            saveUserToken(savedUser, accessToken);
            return AuthenticationResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        }
        return null;
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception e) {
            throw new Exception("Account not found");
        }
        // user successfully authenticated, so generate a token and send it back to the client
        User user = repository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("FATAL: Could not find user in db after successfully authenticating! This could be a potential attack, or the db is down."));
        String accessToken = jwtService.generateTokens(user).get("access_token");
        String refreshToken = jwtService.generateTokens(user).get("refresh_token");
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return AuthenticationResponseDTO.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    public CredentialsCheckResponseDTO checkCredentials(AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            User user = repository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException(""));
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            userResponseDTO.setEmail(user.getEmail());
            userResponseDTO.setFirstName(user.getFirstName());
            userResponseDTO.setLastName(user.getLastName());
            userResponseDTO.setRole(user.getRole());
            return CredentialsCheckResponseDTO.builder().validCredentials(true).userResponseDTO(userResponseDTO).build();
        } catch (Exception e) {
            return CredentialsCheckResponseDTO.builder().validCredentials(false).build();
        }
    }

    public AuthenticationResponseDTO refreshAccessToken(String refreshToken) {
        // the user's email is stored in the subject of the jwt
        User user = repository.findByEmail(jwtService.extractUsername(refreshToken)).orElseThrow(() -> new UsernameNotFoundException("FATAL: User attempted to refresh access token, but the user was not found in the db."));
        var persistedRefreshToken = tokenRepository.findByToken(refreshToken);
        if (!persistedRefreshToken.orElseThrow(() -> new EntityNotFoundException("FATAL: User attempted to refresh access token, but the refresh token was not found in the db.")).isExpired()) {
            String accessToken = jwtService.generateTokens(user).get("access_token");
            String newRefreshToken = jwtService.generateTokens(user).get("refresh_token");
            revokeAllUserTokens(user);
            saveUserToken(user, accessToken);
            return AuthenticationResponseDTO.builder().accessToken(accessToken).refreshToken(newRefreshToken).build();
        }
        throw new RuntimeException("Token expired or not found in the database. Cannot refresh access token.");
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.HTTP_ONLY_COOKIE)
                .expired(false)
                .revoked(false)
                .build();

        System.out.println(token);
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

}
