package com.t2.screening.tenisu.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Repository
public class InMemoryPlayerRepository implements PlayerRepository {
    private final List<Player> players = new ArrayList<>();

    @Data
    private static class Players {
        private List<Player> players;
    }

    public InMemoryPlayerRepository(ObjectMapper objectMapper) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("players.json");) {
            Players container = objectMapper.readValue(is, Players.class);
            players.addAll(container.getPlayers());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Player> findAll() {
        return Collections.unmodifiableList(players);
    }
}
