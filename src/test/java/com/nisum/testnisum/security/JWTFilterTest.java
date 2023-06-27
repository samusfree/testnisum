package com.nisum.testnisum.security;

import com.nisum.testnisum.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

public class JWTFilterTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private JWTFilter jwtFilter;
    private JWTService jwtService;

    @BeforeEach
    void setup() {
        request = Mockito.mock();
        response = Mockito.mock();
        filterChain = Mockito.mock();
        jwtService = Mockito.mock();
        jwtFilter = new JWTFilter(jwtService);
    }

    @DisplayName("Validate Filter valid token")
    @Test
    void testValidateFilter() throws ServletException, IOException {
        Mockito.when(request.getHeader(any())).thenReturn("Bearer dsdsddsd");
        Mockito.when(jwtService.validateAccessToken(any())).thenReturn(true);
        jwtFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(jwtService).validateAccessToken(any());
    }

    @DisplayName("Validate Filter invalid token")
    @Test
    void testInvalidToken() throws ServletException, IOException {
        Mockito.when(request.getHeader(any())).thenReturn("Bearer dsdsddsd");
        Mockito.when(jwtService.validateAccessToken(any())).thenReturn(false);
        jwtFilter.doFilterInternal(request, response, filterChain);
        Mockito.verify(jwtService).validateAccessToken(any());
    }

    @DisplayName("Validate Filter no tkken")
    @Test
    void testNoToken() throws ServletException, IOException {
        Mockito.when(request.getHeader(any())).thenReturn(StringUtils.EMPTY);
        jwtFilter.doFilterInternal(request, response, filterChain);
        Mockito.verifyNoInteractions(jwtService);
    }

    @DisplayName("Validate Filter no bearer")
    @Test
    void testNotBearer() throws ServletException, IOException {
        Mockito.when(request.getHeader(any())).thenReturn("A");
        jwtFilter.doFilterInternal(request, response, filterChain);
        Mockito.verifyNoInteractions(jwtService);
    }
}
