package com.t2.screening.tenisu.infrastructure.mybatis;

import com.t2.screening.tenisu.domain.model.Country;
import com.t2.screening.tenisu.domain.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class MybatisCountryRepository implements CountryRepository {
    private final CountryMapper countryMapper;

    @Nonnull
    @Override
    public List<Country> findAll() {
        return countryMapper.findAll();
    }

    @Override
    public Optional<Country> findByCode(String code) {
        return Optional.ofNullable(countryMapper.findByCode(code));
    }
}
