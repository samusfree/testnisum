package com.nisum.testnisum.service;

import com.nisum.testnisum.dto.UserRequestDTO;

public interface JWTService {
    String getJWTToken(UserRequestDTO user);

    boolean validateAccessToken(String token);

    String getSubject(String token);
}
