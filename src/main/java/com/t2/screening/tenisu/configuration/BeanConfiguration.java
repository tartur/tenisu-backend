package com.t2.screening.tenisu.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t2.screening.tenisu.application.FindPlayersService;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import com.t2.screening.tenisu.infrastructure.InMemoryPlayerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BeanConfiguration {

    @Bean
    public PlayerRepository playerRepository(ObjectMapper objectMapper) {
        return new InMemoryPlayerRepository(objectMapper);
    }

    @Bean
    public FindPlayersService findPlayersService(PlayerRepository playerRepository) {
        return new FindPlayersService(playerRepository);
    }
}
