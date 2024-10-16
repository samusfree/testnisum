package com.nisum.testnisum.security;

import com.nisum.testnisum.service.JWTService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    @Autowired
    public JWTFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);

        if (!jwtService.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthenticationContext(token, request);
        filterChain.doFilter(request, response);
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return !ObjectUtils.isEmpty(header) && header.startsWith("Bearer");
    }

    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String[] parts = header.split(StringUtils.SPACE);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        CustomUserDetails customUserDetails = getUserDetails(token);
        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, null);
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private CustomUserDetails getUserDetails(String token) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setEmail(jwtService.getSubject(token));
        return customUserDetails;
    }
}
