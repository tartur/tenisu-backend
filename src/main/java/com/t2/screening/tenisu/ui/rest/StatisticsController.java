package com.t2.screening.tenisu.ui.rest;

import com.t2.screening.tenisu.application.GetStatisticsService;
import com.t2.screening.tenisu.application.model.Statistics;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final GetStatisticsService statisticsService;

    @GetMapping
    public Statistics getAllStatistics() {
        return statisticsService.handle();
    }
}
