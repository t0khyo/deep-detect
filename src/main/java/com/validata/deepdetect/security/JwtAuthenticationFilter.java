package com.validata.deepdetect.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.validata.deepdetect.exception.JwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    @Lazy
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Decide whether the filter should be applied.
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("No JWT access token in the request or access token format invalid");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Apply filter: authenticate or reject request
        final String jwt = authHeader.substring(7);
        final JWTClaimsSet claims;
        final String username;

        try {
            claims = jwtUtil.validateToken(jwt);
            username = jwtUtil.extractUsername(claims);

            if (username != null && !username.isBlank() && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.error("JWT authentication failed: {}", ex.getMessage());
            throw new JwtTokenException("JWT authentication failed", ex);
        }

        // 3. Invoke the "rest" of the chain
//        log.info("Authenticated user: {}", username);
        filterChain.doFilter(request, response);
    }
}
