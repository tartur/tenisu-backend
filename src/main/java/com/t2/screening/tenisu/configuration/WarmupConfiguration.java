package com.t2.screening.tenisu.configuration;

import com.t2.screening.tenisu.application.FindPlayersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
class WarmupConfiguration {


    @Bean
    ApplicationRunner warmupRunner(FindPlayersService findPlayersService) {
        return args -> {
            try {
                findPlayersService.findAllSortedByRank();
            } catch (Exception e) {
                log.warn("Warmup failed", e);
            }
        };
    }

}
