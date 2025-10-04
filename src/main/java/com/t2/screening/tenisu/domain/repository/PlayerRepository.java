package com.t2.screening.tenisu.domain.repository;

import com.t2.screening.tenisu.domain.model.Player;

import java.util.List;

public interface PlayerRepository {

    List<Player> findAll();
}
