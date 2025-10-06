package com.t2.screening.tenisu.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t2.screening.tenisu.domain.model.Country;
import com.t2.screening.tenisu.domain.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class InMemoryCountryRepository implements CountryRepository {
    private final List<Country> countries = new ArrayList<>();

    @Nonnull
    @Override
    public List<Country> findAll() {
        return Collections.unmodifiableList(countries);
    }

    @Override
    public Optional<Country> findByCode(String code) {
        return countries.stream().filter(c -> c.getCode().equals(code)).findFirst();
    }

    private record Countries(List<Country> countries) {
    }

    public InMemoryCountryRepository(ObjectMapper objectMapper, String initFile) {
        log.info("Loading countries from JSON file {}", initFile);
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(initFile)) {
            Countries container = objectMapper.readValue(is, Countries.class);
            countries.addAll(container.countries());
            log.info("{} countries loaded", countries.size());
        } catch (IOException e) {
            log.error("Failed to load countries from JSON file {}", initFile);
            throw new RuntimeException(e);
        }
    }
}
