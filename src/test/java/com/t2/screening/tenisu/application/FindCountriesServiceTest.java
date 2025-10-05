package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.domain.model.Country;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.model.PlayerData;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCountriesServiceTest {
    static final Country TUN = new Country("TUN", "url");
    static final Country FRA = new Country("FRA", "url");
    static final Country ESP = new Country("ESP", "url");

    @Mock
    PlayerRepository playerRepository;
    @InjectMocks
    FindCountriesService sut;

    @Test
    void when_findAllSortedByWinRatioFirst_with_empty_db_should_return_empty_list() {
        when(playerRepository.findAll()).thenReturn(List.of());

        assertThat(sut.findAllSortedByWinRatioFirst()).isEmpty();
    }

    @Test
    void when_findAllSortedByWonMatchesFirst_then_return_country_list_reverse_sorted_by_winRatio() {
        when(playerRepository.findAll()).thenReturn(List.of(
                rankedPLayer(30, TUN, 0, 1, 0, 1, 1),
                rankedPLayer(50, FRA, 0, 1, 0, 0, 1),
                rankedPLayer(20, ESP, 0, 1, 1, 0, 1),
                rankedPLayer(102, ESP, 0, 0, 0, 0, 1),
                rankedPLayer(1, ESP, 1, 1, 1, 1, 1),
                rankedPLayer(11, FRA, 1, 1, 0, 1, 1),
                rankedPLayer(120, FRA, 0, 0, 0, 1, 0)
        ));

        assertThat(sut.findAllSortedByWinRatioFirst()).containsExactly(ESP, TUN, FRA);
    }

    private Player rankedPLayer(int rank, Country country, int... lastResults) {
        return Player.builder().country(country).data(PlayerData.builder().rank(rank).last(lastResults).build()).build();
    }
}