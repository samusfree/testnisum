package com.nisum.testnisum.service.impl;

import com.nisum.testnisum.dto.UserRequestDTO;
import com.nisum.testnisum.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JWTServiceImpl implements JWTService {

    private final Integer expirationTime;
    private final String secretKey;

    public JWTServiceImpl(@Value("${app.jwt.secret}") String secretKey,
                          @Value("${app.jwt.expiration}") Integer expirationTime) {
        this.secretKey = secretKey;
        this.expirationTime = expirationTime;
    }

    @Override
    public String getJWTToken(UserRequestDTO user) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(
                "ROLE_USER");
        return Jwts.builder()
                .claims()
                .id("NisumJWT")
                .subject(user.email())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .add("authorities", grantedAuthorities.stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .and()
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parse(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("JWT expired: ", ex);
        } catch (IllegalArgumentException ex) {
            log.error("Token is null, empty or only whitespace", ex);
        } catch (MalformedJwtException ex) {
            log.error("JWT is invalid", ex);
        } catch (Exception ex) {
            log.error("JWT unsupported", ex);
        }
        return false;
    }

    @Override
    public String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
