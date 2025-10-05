package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.domain.exception.PlayerNotFoundException;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FindPlayersService {
    private final PlayerRepository playerRepository;

    public List<Player> findAllSortedByRank() {
        log.debug("find all players");
        return playerRepository.findAll().stream()
                .sorted(Comparator.comparingInt(p -> p.getData().getRank()))
                .toList();
    }

    public Player findById(Long id) {
        log.debug("find player by id: {}", id);
        return playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
    }
}
