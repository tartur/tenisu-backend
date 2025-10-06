package com.t2.screening.tenisu.infrastructure.mybatis;

import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class MybatisPlayerRepository implements PlayerRepository {
    private final PlayerMapper playerMapper;

    @Nonnull
    @Override
    public List<Player> findAll() {
        return playerMapper.findAll();
    }

    @Override
    public Optional<Player> findById(Long id) {
        return Optional.ofNullable(playerMapper.findById(id));
    }

    @Override
    public Player save(Player player) {
        if (playerMapper.insertPlayer(player) > 0 && playerMapper.insertPlayerData(player) > 0) {
            return playerMapper.findById(player.getId());
        } else {
            log.error("Player insertion failed: {}", player);
            throw new RuntimeException("Player insertion failed");
        }
    }
}
