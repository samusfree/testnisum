package com.nisum.testnisum.service;


import com.nisum.testnisum.dto.UserDTO;
import com.nisum.testnisum.dto.UserRequestDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserRequestDTO user);

    List<UserDTO> listUsers();
}
