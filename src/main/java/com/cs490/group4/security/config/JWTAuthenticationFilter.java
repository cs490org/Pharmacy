package com.cs490.group4.security.config;

import com.cs490.group4.security.config.token.TokenRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, EntityNotFoundException {
        String jwt = null;
        String email = null;

        if(request.getRequestURI().equals("/auth/authenticate") || request.getRequestURI().equals("/auth/register")){
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final List<Cookie> cookies = List.of(request.getCookies());

            for (int i = 0; i < cookies.size(); i++) {
                /**
                 * TODO: Figure out why httpOnly is not coming up as true?
                 * It needs to be true to be secure! Otherwise any cookie can access!
                 */
                if (cookies.get(i).getName().equals("access_token")) {
                    System.out.println("access token found");
                    System.out.println("http only: " + cookies.get(i).isHttpOnly());
                    jwt = cookies.get(i).getValue();

                    if (!jwtService.isTokenExpired(jwt)) {
                        email = jwtService.extractUsername(jwt);
                    }
                } else if (i == cookies.size() - 1 && jwt == null) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        } catch (NullPointerException e) {
            filterChain.doFilter(request, response);
            return;
        }

        /**
         *  check if the user is authenticated from the SecurityContextHolder
         *  that way we don't keep checking the jwt every time
         */
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            System.out.println(userDetails);
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            System.out.println(isTokenValid);
            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
