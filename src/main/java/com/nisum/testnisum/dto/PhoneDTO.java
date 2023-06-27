package com.nisum.testnisum.dto;

import jakarta.validation.constraints.NotBlank;

public record PhoneDTO(@NotBlank(message = "Number cannot be empty")
                       String number,
                       @NotBlank(message = "City code cannot be empty")
                       String cityCode,
                       @NotBlank(message = "Country code cannot be empty")
                       String countryCode) {
}
