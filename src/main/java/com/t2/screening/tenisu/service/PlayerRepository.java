package com.t2.screening.tenisu.service;

import com.t2.screening.tenisu.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {

    List<Player> findAll();

    Optional<Player> findById(Long id);
}
