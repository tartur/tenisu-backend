package com.t2.screening.tenisu.domain.repository;

import com.t2.screening.tenisu.domain.model.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface PlayerRepository {

    @Nonnull
    List<Player> findAll();

    Optional<Player> findById(Long id);
}
