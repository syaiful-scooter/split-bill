package com.syaiful.split.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PersonsDto {
    private int id;

    @NotBlank
    private String firstName;

    private String lastName;

    private BigDecimal amount;

    private String email;
}
