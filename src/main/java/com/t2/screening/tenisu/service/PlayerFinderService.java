package com.t2.screening.tenisu.service;

import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerFinderService {
    private final PlayerRepository playerRepository;

    public List<Player> findAllSorted() {
        return playerRepository.findAll().stream()
                .sorted(Comparator.comparingInt(p -> p.getData().getRank()))
                .toList();
    }
}
