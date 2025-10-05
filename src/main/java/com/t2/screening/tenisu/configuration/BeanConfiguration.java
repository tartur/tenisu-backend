package com.t2.screening.tenisu.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.t2.screening.tenisu.application.FindCountriesService;
import com.t2.screening.tenisu.application.FindPlayersService;
import com.t2.screening.tenisu.application.GetStatisticsService;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import com.t2.screening.tenisu.infrastructure.InMemoryPlayerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BeanConfiguration {

    @Bean
    PlayerRepository playerRepository(ObjectMapper objectMapper) {
        return new InMemoryPlayerRepository(objectMapper);
    }

    @Bean
    FindPlayersService findPlayersService(PlayerRepository playerRepository) {
        return new FindPlayersService(playerRepository);
    }

    @Bean
    FindCountriesService findCountriesService(PlayerRepository playerRepository) {
        return new FindCountriesService(playerRepository);
    }

    @Bean
    GetStatisticsService calculatePlayersStatisticsService(PlayerRepository playerRepository,
                                                           FindCountriesService findCountriesService) {
        return new GetStatisticsService(playerRepository, findCountriesService);
    }
}
