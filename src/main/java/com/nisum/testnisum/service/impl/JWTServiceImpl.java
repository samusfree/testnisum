package com.nisum.testnisum.service.impl;

import com.nisum.testnisum.dto.UserRequestDTO;
import com.nisum.testnisum.service.JWTService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JWTServiceImpl implements JWTService {
    private final String secretKey;

    public JWTServiceImpl(@Value("${app.jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String getJWTToken(UserRequestDTO user) {

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        return Jwts
                .builder()
                .setId("NisumJWT")
                .setSubject(user.email())
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .claim("name", user.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS512).compact();
    }

    @Override
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).build().parseClaimsJws(token);
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
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).build()
                .parseClaimsJws(token).getBody();
    }
}
