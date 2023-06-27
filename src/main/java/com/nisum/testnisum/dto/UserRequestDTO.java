package com.nisum.testnisum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record UserRequestDTO(@NotBlank(message = "Name code cannot be empty")
                             String name,
                             @Email(message = "Email should be valid")
                             @NotBlank(message = "Email code cannot be empty")
                             String email,
                             @NotBlank(message = "Password code cannot be empty")
                             @Pattern(regexp = "^(?=.*?\\d.*\\d)(?=\\w*[A-Z])(?=\\w*[a-z])\\S{8,16}$",
                                     message = "Password must have at least one upper case letter, one lower case letter and two numbers. " +
                                             "Also the size must be between 8 and 16")
                             String password,
                             @NotEmpty(message = "Phones must have at least one value")
                             @Valid
                             List<PhoneDTO> phones) {
}