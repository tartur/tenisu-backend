package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.application.model.Statistics;
import com.t2.screening.tenisu.application.service.CalculateBMIService;
import com.t2.screening.tenisu.application.service.StatisticsCalculator;
import com.t2.screening.tenisu.domain.model.Country;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GetStatisticsService {
    private final PlayerRepository playerRepository;
    private final FindCountriesService countriesService;
    private final StatisticsCalculator statisticsCalculator = new StatisticsCalculator();
    private final CalculateBMIService calculateBMIService = new CalculateBMIService();


    /**
     * Calculate the statistics over all players
     */
    @Nonnull
    public Statistics handle() {
        log.info("build statistics");
        List<Player> players = playerRepository.findAll();
        log.debug("Players found: {}", players.size());
        double averageBMI = calculateAverageBMI(players);
        double medianHeight = calculateMedianHeight(players);
        Country country = countriesService.findAllSortedByWinRatioFirst().stream().findFirst().orElse(null);
        log.debug("best country: {}, averageBMI: {}, medianHeight: {}", country, averageBMI, medianHeight);
        return new Statistics(country, averageBMI, medianHeight);
    }

    /**
     * @return NaN if no player found and the median height (cm) otherwise
     */
    private double calculateMedianHeight(List<Player> players) {
        return statisticsCalculator.calculateMedian(
                players.stream().map(p -> Double.valueOf(p.getData().getHeight())).toList()
        );
    }

    /**
     * Calculate the average BMI (Body Mass Index) of all players
     * @return NaN if no player is found, otherwise the average BMI
     */
    private double calculateAverageBMI(List<Player> players) {
        return statisticsCalculator.calculateAverage(
                players.stream().map(calculateBMIService::calculate).toList()
        );
    }
}
