package com.t2.screening.tenisu.application.model;

import com.t2.screening.tenisu.domain.model.Country;

public record Statistics(Country bestWinRatioCountry, double averageBMI, double medianHeight) {
}
