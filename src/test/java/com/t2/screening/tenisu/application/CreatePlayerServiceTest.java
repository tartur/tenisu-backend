package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.domain.exception.CountryNotFoundException;
import com.t2.screening.tenisu.domain.model.Country;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.CountryRepository;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CreatePlayerServiceTest {
    @Mock
    PlayerRepository playerRepository;
    @Mock
    CountryRepository countryRepository;
    @InjectMocks
    CreatePlayerService createPlayerService;

    @BeforeEach
    public void setUp() {
        when(countryRepository.findByCode("FRA")).thenReturn(of(new Country("FRA", "uri")));
        when(countryRepository.findByCode("ESP")).thenReturn(empty());
        when(playerRepository.save(any(Player.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void when_createPlayer_fill_short_name() {
        Player created = createPlayerService.create(Player.builder()
                .firstname("Toto").lastname("Tata").country(new Country("FRA", null))
                .build());

        assertThat(created.getShortname()).isNotEmpty().isEqualTo("T.TAT");
    }

    @Test
    void when_createPlayer_fill_country_from_referential() {
        Player created = createPlayerService.create(Player.builder()
                .firstname("Toto").lastname("Tata").country(new Country("FRA", null))
                .build());

        assertThat(created.getCountry()).isEqualTo(new Country("FRA", "uri"));
    }

    @Test
    void when_returns_empty_country_throw_CountryNotFoundException() {
        assertThatThrownBy(() -> createPlayerService.create(Player.builder()
                .firstname("Toto").lastname("Tata").country(new Country("ESP", null))
                .build())).isInstanceOf(CountryNotFoundException.class);
    }

    @Test
    void when_create_player_delegate_to_infrastrcture() {
        when(countryRepository.findByCode("FRA")).thenReturn(of(new Country("FRA", "uri")));

        createPlayerService.create(Player.builder()
                .firstname("Toto").lastname("Tata").country(new Country("FRA", null))
                .build());

        verify(playerRepository).save(any());
    }
}