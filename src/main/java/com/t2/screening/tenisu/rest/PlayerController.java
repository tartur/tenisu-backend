package com.t2.screening.tenisu.rest;

import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.service.PlayerFinderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
