package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.domain.exception.PlayerNotFoundException;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.model.PlayerData;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindPlayersServiceTest {
    @Mock
    private PlayerRepository playerRepository;
    @InjectMocks
    private FindPlayersService findPlayersService;

    @Test
    void when_findAll_Sorted_thenReturn_all_players_sorted_by_rank() {
        when(playerRepository.findAll()).thenReturn(List.of(
                Player.builder().id(1L).data(PlayerData.builder().rank(30).build()).build(),
                Player.builder().id(2L).data(PlayerData.builder().rank(60).build()).build(),
                Player.builder().id(3L).data(PlayerData.builder().rank(2).build()).build()
        ));

        List<Player> players = findPlayersService.findAllSortedByRank();

        assertThat(players).hasSize(3).map(Player::getId).containsSequence(3L, 1L, 2L);
    }

    @Test
    void when_findById_exists_return_the_object() {
        when(playerRepository.findById(1L)).thenReturn(
                of(Player.builder().id(1L).data(PlayerData.builder().rank(30).build()).build())
        );

        assertThat(findPlayersService.findById(1L)).isNotNull();
    }

    @Test
    void when_findById_does_not_exist_throw() {
        when(playerRepository.findById(1L)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> findPlayersService.findById(1L)).isInstanceOf(PlayerNotFoundException.class);
    }
}