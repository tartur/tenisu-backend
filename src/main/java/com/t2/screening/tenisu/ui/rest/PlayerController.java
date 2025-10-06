package com.t2.screening.tenisu.ui.rest;

import com.t2.screening.tenisu.application.CreatePlayerService;
import com.t2.screening.tenisu.application.FindPlayersService;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.ui.rest.dto.AllReadPlayersDto;
import com.t2.screening.tenisu.ui.rest.dto.PlayerMapper;
import com.t2.screening.tenisu.ui.rest.dto.ReadPlayerDto;
import com.t2.screening.tenisu.ui.rest.dto.WritePlayerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/players")
public class PlayerController {
    private final FindPlayersService findPlayersService;
    private final CreatePlayerService createPlayerService;
    private final PlayerMapper playerMapper;

    @GetMapping
    public AllReadPlayersDto getAllPlayers() {
        List<Player> players = findPlayersService.findAllSortedByRank();
        return new AllReadPlayersDto(players.stream().map(playerMapper::toReadDto).toList());
    }

    @GetMapping("/{id}")
    public ReadPlayerDto getPlayer(@PathVariable("id") Long id) {
        Player player = findPlayersService.findById(id);
        return playerMapper.toReadDto(player);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('SCOPE_tenisu-api/write')")
    public ReadPlayerDto createPlayer(@Validated @RequestBody WritePlayerDto player) {
        Player toBeCreated = playerMapper.toDomainObject(player);
        Player created = createPlayerService.create(toBeCreated);
        return playerMapper.toReadDto(created);
    }
}
