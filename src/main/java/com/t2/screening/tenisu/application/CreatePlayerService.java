package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.domain.exception.CountryNotFoundException;
import com.t2.screening.tenisu.domain.model.Country;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.CountryRepository;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class CreatePlayerService {
    private final PlayerRepository playerRepository;
    private final CountryRepository countryRepository;

    @Transactional
    public Player create(Player player) {
        Country refCountry = countryRepository.findByCode(player.getCountry().getCode())
                .orElseThrow(() -> new CountryNotFoundException("Country not found"));

        player.setCountry(refCountry);

        if (StringUtils.isNotBlank(player.getFirstname()) && StringUtils.isNotBlank(player.getLastname())) {
            char firstInitial = Character.toUpperCase(player.getFirstname().charAt(0));
            String lastTrigram = player.getLastname().substring(0, Math.min(3, player.getLastname().length())).toUpperCase();
            player.setShortname(String.format("%s.%s", firstInitial, lastTrigram));
        }
        return playerRepository.save(player);
    }
}
