package com.example.campusbackend.filter;

import com.example.campusbackend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    private static final List<String> WHITELIST_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register",
        "/error",
        "/api/resource",
        "/api/resource/available",
        "/api/resource/type",
        "/api/scenic",
        "/api/navigation",
        "/api/auth"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        final String requestURI = request.getRequestURI();
        
        log.info("Processing request for URI: {}", requestURI);
        log.debug("Authorization header: {}", authHeader);

        if (isWhitelistedPath(requestURI)) {
            log.info("Request URI {} is whitelisted, skipping authentication", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No valid Authorization header found for URI: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);
        log.debug("Extracted username from token: {}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Loading user details for username: {}", username);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("Token is valid for user: {}", username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Authentication successful for user: {}", username);
            } else {
                log.warn("Invalid token for user: {}", username);
            }
        } else {
            log.debug("No username found in token or user already authenticated");
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isWhitelistedPath(String path) {
        boolean isWhitelisted = WHITELIST_PATHS.stream().anyMatch(whitelistedPath -> 
            path.startsWith(whitelistedPath) || path.equals(whitelistedPath)
        );
        log.debug("Path {} is whitelisted: {}", path, isWhitelisted);
        return isWhitelisted;
    }
} 