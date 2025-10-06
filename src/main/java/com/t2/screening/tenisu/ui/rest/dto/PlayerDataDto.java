package com.t2.screening.tenisu.ui.rest.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record PlayerDataDto(
        @Positive
        int rank,
        @PositiveOrZero
        int points,
        @Positive
        @Min(2L)
        int height,
        @Positive
        @Min(1000L)
        int weight,
        //FIXME: age should be removed from creation and replaced by a birthdate
        @PositiveOrZero
        int age,
        @Size(max = 5)
        List<@Min(0) @Max(1) Integer> last
) {
}
