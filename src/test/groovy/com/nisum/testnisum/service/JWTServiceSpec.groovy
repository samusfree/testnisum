package com.nisum.testnisum.service


import com.nisum.testnisum.service.impl.JWTServiceImpl
import com.nisum.testnisum.util.CreateObjectsUtil
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification
import spock.lang.Subject

class JWTServiceImplSpec extends Specification {
    @Subject
    JWTService jwtService

    def setup() {
        jwtService = new JWTServiceImpl(CreateObjectsUtil.SECRET_KEY, 60000)
    }

    def "generateToken should return a valid JWT token"() {
        given:

        when:
        String token = jwtService.getJWTToken(CreateObjectsUtil.getUserRequestDTO())

        then:
        token != null
        jwtService.getSubject(token) == CreateObjectsUtil.getUserRequestDTO().email()
    }

    def "validateAccessToken should return true for a valid token"() {
        given:
        String token = jwtService.getJWTToken(CreateObjectsUtil.getUserRequestDTO())

        when:
        boolean isValid = jwtService.validateAccessToken(token)

        then:
        isValid
    }

    def "validateAccessToken should return false for an invalid token"() {
        given:
        String token = "invalidToken"

        when:
        boolean isValid = jwtService.validateAccessToken(token)

        then:
        !isValid
    }

    def "validateAccessToken should return false for an invalid key"() {
        given:
        String token = "invalidToken"
        jwtService = new JWTServiceImpl(StringUtils.EMPTY, 60000)

        when:
        boolean isValid = jwtService.validateAccessToken(token)

        then:
        !isValid
    }

    def "validateAccessToken should return false for an empty token"() {
        given:

        when:
        boolean isValid = jwtService.validateAccessToken(StringUtils.EMPTY)

        then:
        !isValid
    }

    def "validateAccessToken should return false for expired token"() {
        given:
        jwtService = new JWTServiceImpl(CreateObjectsUtil.SECRET_KEY, -1000)
        def token = jwtService.getJWTToken(CreateObjectsUtil.getUserRequestDTO())

        when:
        boolean isValid = jwtService.validateAccessToken(token)

        then:
        !isValid
    }
}
