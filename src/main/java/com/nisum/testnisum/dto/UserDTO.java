package com.nisum.testnisum.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public record UserDTO(UUID id, String name, String email, String password, LocalDateTime created,
                      LocalDateTime modified,
                      LocalDateTime lastLogin, String token, boolean active, List<PhoneDTO> phones) {
}
