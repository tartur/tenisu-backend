package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.domain.exception.PlayerNotFoundException;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class FindPlayersService {
    private final PlayerRepository playerRepository;

    public List<Player> findAllSorted() {
        return playerRepository.findAll().stream()
                .sorted(Comparator.comparingInt(p -> p.getData().getRank()))
                .toList();
    }

    public Player findById(Long id) {
        return playerRepository.findById(id).orElseThrow(PlayerNotFoundException::new);
    }
}
