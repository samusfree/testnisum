package com.nisum.testnisum.security

import com.nisum.testnisum.service.JWTService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification
import spock.lang.Subject

class JWTFilterSpec extends Specification {

    HttpServletRequest request = Mock()
    HttpServletResponse response = Mock()
    FilterChain filterChain = Mock()
    JWTService jwtService = Mock()

    @Subject
    JWTFilter jwtFilter = new JWTFilter(jwtService)

    def "Validate Filter valid token"() {
        given:
        request.getHeader("Authorization") >> "Bearer validToken"
        jwtService.validateAccessToken(_ as String) >> true

        when:
        jwtFilter.doFilterInternal(request, response, filterChain)

        then:
        1 * jwtService.validateAccessToken(_) >> true
        1 * jwtService.getSubject(_)
        1 * filterChain.doFilter(request, response)
    }

    def "Validate Filter invalid token"() {
        given:
        request.getHeader("Authorization") >> "Bearer invalidToken"
        jwtService.validateAccessToken("invalidToken") >> false

        when:
        jwtFilter.doFilterInternal(request, response, filterChain)

        then:
        1 * jwtService.validateAccessToken("invalidToken")
        0 * jwtService.getSubject(_)
    }

    def "Validate Filter no token"() {
        given:
        request.getHeader("Authorization") >> StringUtils.EMPTY

        when:
        jwtFilter.doFilterInternal(request, response, filterChain)

        then:
        0 * jwtService.validateAccessToken(_)
        0 * jwtService.getSubject(_)
    }

    def "Validate Filter no bearer"() {
        given:
        request.getHeader("Authorization") >> "NotBearerToken"

        when:
        jwtFilter.doFilterInternal(request, response, filterChain)

        then:
        0 * jwtService.validateAccessToken(_)
        0 * jwtService.getSubject(_)
    }

    def "Validate Filter bearer without token"() {
        given:
        request.getHeader("Authorization") >> "Bearer"
        jwtService.validateAccessToken("") >> false


        when:
        jwtFilter.doFilterInternal(request, response, filterChain)

        then:
        1 * jwtService.validateAccessToken("") >> false
        0 * jwtService.getSubject(_)
    }
}
