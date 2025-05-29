package org.example.pionerpixeltest.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @NotEmpty
    private Set<String> emails;

    @NotEmpty
    private Set<String> phones;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private BigDecimal initialBalance;


}
