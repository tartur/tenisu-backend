package com.t2.screening.tenisu.ui.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import static jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

/**
 * DTO for player creation request
 *
 * @param firstname
 * @param lastname
 * @param sex         M for male and F for female
 * @param countryCode private referential alpha-3 country code
 * @param picture     url
 * @param data
 */
public record WritePlayerDto(
        @NotBlank
        String firstname,
        @NotBlank
        String lastname,
        @Pattern(regexp = "M|F", flags = CASE_INSENSITIVE)
        String sex,
        @Size(min = 3, max = 3)
        String countryCode,
        @URL
        String picture,
        @NotNull
        @Valid
        PlayerDataDto data
) {
}
