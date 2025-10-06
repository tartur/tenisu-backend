package com.t2.screening.tenisu.ui.rest.dto;

import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.model.Sex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    String M = "M";
    String F = "F";

    @Mapping(target = "country.code", source = "countryCode")
    Player toDomainObject(WritePlayerDto player);

    ReadPlayerDto toReadDto(Player player);

    default Sex toSexEnum(String sex) {
        return switch (sex) {
            case M -> Sex.MALE;
            case F -> Sex.FEMALE;
            default -> throw new IllegalArgumentException("Invalid sex: " + sex + " ,valid values are M or F" + sex);
        };
    }

    default String toSexString(Sex sex) {
        return switch (sex) {
            case MALE -> M;
            case FEMALE -> F;
        };
    }
}
