package com.t2.screening.tenisu.adapter.controller;

import com.t2.screening.tenisu.model.Player;
import com.t2.screening.tenisu.service.PlayerFinderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerFinderService playerFinderService;

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerFinderService.findAllSorted();
    }

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable("id") Long id) {
        return playerFinderService.findById(id);
    }
}
