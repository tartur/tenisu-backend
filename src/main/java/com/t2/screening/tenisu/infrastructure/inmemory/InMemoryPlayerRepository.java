package com.t2.screening.tenisu.infrastructure.inmemory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class InMemoryPlayerRepository implements PlayerRepository {
    public static final String PLAYERS_JSON = "players.json";
    private final List<Player> players = new ArrayList<>();

    private record Players(List<Player> players) {
    }

    public InMemoryPlayerRepository(ObjectMapper objectMapper) {
        log.info("Loading players from JSON file {}", PLAYERS_JSON);
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(PLAYERS_JSON)) {
            Players container = objectMapper.readValue(is, Players.class);
            players.addAll(container.players());
            log.info("{} players loaded", players.size());
        } catch (IOException e) {
            log.error("Failed to load players from JSON file {}", PLAYERS_JSON);
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public List<Player> findAll() {
        return Collections.unmodifiableList(players);
    }

    @Override
    public Optional<Player> findById(Long id) {
        return players.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    @Override
    public Player save(Player player) {
        long id = nextId();
        player.setId(id);
        players.add(player);
        return player;
    }

    private long nextId() {
        return players.stream().mapToLong(Player::getId).max().orElse(0) + 1;
    }
}
