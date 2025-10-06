package com.t2.screening.tenisu.configuration;

import com.t2.screening.tenisu.application.CreatePlayerService;
import com.t2.screening.tenisu.application.FindCountriesService;
import com.t2.screening.tenisu.application.FindPlayersService;
import com.t2.screening.tenisu.application.GetStatisticsService;
import com.t2.screening.tenisu.domain.repository.CountryRepository;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import com.t2.screening.tenisu.infrastructure.mybatis.CountryMapper;
import com.t2.screening.tenisu.infrastructure.mybatis.MybatisCountryRepository;
import com.t2.screening.tenisu.infrastructure.mybatis.MybatisPlayerRepository;
import com.t2.screening.tenisu.infrastructure.mybatis.PlayerMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BeanConfiguration {

    @Bean
    PlayerRepository playerRepository(PlayerMapper playerMapper) {
        //return new InMemoryPlayerRepository(objectMapper);
        return new MybatisPlayerRepository(playerMapper);
    }

    @Bean
    CountryRepository countryRepository(CountryMapper countryMapper) {
        //return new InMemoryCountryRepository(objectMapper, "countries.json");
        return new MybatisCountryRepository(countryMapper);
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

    @Bean
    CreatePlayerService createPlayerService(PlayerRepository playerRepository, CountryRepository countryRepository) {
        return new CreatePlayerService(playerRepository, countryRepository);
    }
}
