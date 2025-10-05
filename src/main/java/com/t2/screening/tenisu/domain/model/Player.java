package com.t2.screening.tenisu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Player {
    private Long id;
    private String firstname;
    private String lastname;
    private String shortname;
    private Sex sex;
    private Country country;
    private String picture;
    private PlayerData data;
}
