package com.t2.screening.tenisu.domain.repository;

import com.t2.screening.tenisu.domain.model.Country;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface CountryRepository {
    @Nonnull
    List<Country> findAll();

    Optional<Country> findByCode(String code);
}
