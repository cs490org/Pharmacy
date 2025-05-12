package com.cs490.group4.security.config;

import com.cs490.group4.security.Role;
import com.cs490.group4.security.User;
import com.cs490.group4.security.config.token.TokenRepository;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static com.cs490.group4.constants.TokenConstants.*;


@Service
@RequiredArgsConstructor
public class JWTService {

    private static Dotenv dotenv = Dotenv.load();

    @FieldNameConstants.Exclude
    private static final String SECRET_KEY = dotenv.get("JWT_SIGNING_SECRET");

    @Autowired
    private TokenRepository tokenRepository;

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public Map<String, String> generateTokens(User userDetails) {
        return generateTokens(new HashMap<>(), userDetails);
    }

    public Map<String, String> generateTokens(Map<String, Object> extraClaims, User userDetails) {
        Map<String, String> tokens = new HashMap<>();

        String accessToken, refreshToken;
//        if (userDetails.getRole().equals(Role.ADMIN)) {
        if (userDetails.getRole().equals(Role.PHARMACIST)) {
            accessToken = Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).setIssuedAt(
                            new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + ADMIN_ACCESS_TOKEN_EXPIRATION))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

            refreshToken = Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).setIssuedAt(
                            new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + ADMIN_REFRESH_TOKEN_EXPIRATION))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
        } else {
            accessToken = Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).setIssuedAt(
                            new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + USER_ACCESS_TOKEN_EXPIRATION))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

            refreshToken = Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername()).setIssuedAt(
                            new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + USER_REFRESH_TOKEN_EXPIRATION))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
        }

        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return tokens;
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(jwt);
    }

    public boolean isTokenExpired(String jwt) {
        try {
            return extractExpiration(jwt).before(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public boolean isTokenRevoked(String jwt) {
        try {
            return tokenRepository.findByToken(jwt).orElseThrow().isRevoked();
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    private Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwt) throws ExpiredJwtException {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(jwt).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
