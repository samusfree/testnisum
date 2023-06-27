package com.nisum.testnisum.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public record UserDTO(UUID id, String name, String email, String password, LocalDate created, LocalDate modified,
                      LocalDate lastLogin, String token, boolean active, List<PhoneDTO> phones) {
}
