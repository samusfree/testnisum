package com.nisum.testnisum.service;


import com.nisum.testnisum.dto.UserDTO;
import com.nisum.testnisum.dto.UserRequestDTO;

public interface UserService {
    UserDTO createUser(UserRequestDTO user);
}
