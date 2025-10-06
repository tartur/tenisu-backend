package com.t2.screening.tenisu.ui.rest.dto;

import com.t2.screening.tenisu.domain.model.Country;

/**
 * DTO for player creation request
 *
 * @param firstname
 * @param lastname
 * @param sex       M for male and F for female
 * @param country
 * @param picture   url
 * @param data
 */
public record ReadPlayerDto(
        long id,
        String firstname,
        String lastname,
        String shortname,
        String sex,
        Country country,
        String picture,
        PlayerDataDto data
) {
}
