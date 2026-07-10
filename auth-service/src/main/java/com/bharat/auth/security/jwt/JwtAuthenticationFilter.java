package com.bharat.auth.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        /*
         * If the Authorization header is missing
         * or does not start with "Bearer ",
         * continue without authenticating the user.
         */
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        /*
         * Remove "Bearer " from the header.
         */
        String jwtToken = authorizationHeader.substring(7);

        /*
         * Extract the email stored in the JWT subject.
         */
        String email = jwtService.extractEmail(jwtToken);

        /*
         * Authenticate only when:
         *
         * 1. Email exists in the token.
         * 2. Spring Security has not already authenticated the request.
         */
        if (email != null
                && SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(email);

            /*
             * Validate:
             *
             * 1. JWT belongs to this user.
             * 2. JWT signature is valid.
             * 3. JWT is not expired.
             */
            if (jwtService.isTokenValid(
                    jwtToken,
                    userDetails.getUsername()
            )) {

                UsernamePasswordAuthenticationToken
                        authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                /*
                 * Tell Spring Security:
                 *
                 * "This request contains a valid JWT,
                 * and this user is authenticated."
                 */
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authenticationToken
                        );
            }
        }

        /*
         * Continue to the next filter.
         */
        filterChain.doFilter(request, response);
    }
}
