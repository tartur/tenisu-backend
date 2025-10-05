package com.t2.screening.tenisu.ui.rest;

import com.t2.screening.tenisu.ui.rest.dto.AllPlayers;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.application.FindPlayersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/players")
public class PlayerController {
    private final FindPlayersService findPlayersService;

    @GetMapping
    public AllPlayers getAllPlayers() {
        return new AllPlayers(findPlayersService.findAllSortedByRank());
    }

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable("id") Long id) {
        return findPlayersService.findById(id);
    }
}
