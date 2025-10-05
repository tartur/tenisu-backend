package com.t2.screening.tenisu.adapter.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t2.screening.tenisu.model.Player;
import com.t2.screening.tenisu.service.PlayerRepository;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


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

    @Override
    public Optional<Player> findById(Long id) {
        return players.stream().filter(p -> p.getId().equals(id)).findFirst();
    }
}
