package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.domain.model.Country;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.model.PlayerData;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetStatisticsServiceTest {
    static final Offset<Double> OFFSET = Offset.offset(0.01);
    static final Country FRA = new Country("FRA", "url");
    static final Country ESP = new Country("ESP", "url");
    @Mock
    FindCountriesService countriesService;
    @Mock
    PlayerRepository playerRepository;
    @InjectMocks
    GetStatisticsService sut;

    @Test
    void statistics_if_empty_db() {
        when(countriesService.findAllSortedByWinRatioFirst()).thenReturn(Collections.emptyList());

        assertThat(sut.handle())
                .hasFieldOrPropertyWithValue("bestWinRatioCountry", null)
                .hasFieldOrPropertyWithValue("averageBMI", Double.NaN)
                .hasFieldOrPropertyWithValue("medianHeight", Double.NaN);
    }

    @Test
    void check_medianHeight() {
        when(playerRepository.findAll()).thenReturn(List.of(
                Player.builder().data(PlayerData.builder().height(180).build()).build(),
                Player.builder().data(PlayerData.builder().height(151).build()).build()
        ));

        assertThat(sut.handle().medianHeight()).isEqualTo(165.5, OFFSET);
    }

    @Test
    void check_avgBMI() {
        when(playerRepository.findAll()).thenReturn(List.of(
                Player.builder().data(PlayerData.builder().weight(80000).height(180).build()).build(),
                Player.builder().data(PlayerData.builder().weight(70000).height(151).build()).build()
        ));

        assertThat(sut.handle().averageBMI()).isEqualTo(27.7, OFFSET);
    }

    @Test
    void check_bestWinRatioCountry() {
        when(countriesService.findAllSortedByWinRatioFirst()).thenReturn(List.of(FRA, ESP));

        assertThat(sut.handle().bestWinRatioCountry()).isEqualTo(FRA);
    }
}