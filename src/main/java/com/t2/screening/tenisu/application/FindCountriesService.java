package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.domain.model.Country;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
public class FindCountriesService {
    private final PlayerRepository playerRepository;

    public List<Country> findAllSortedByWinRatioFirst() {
        log.debug("find countries sorted by win ratio first");
        List<Player> players = playerRepository.findAll();
        Map<Country, IntStream> byCountryresults = players.stream().collect(toMap(
                Player::getCountry,
                p -> IntStream.of(p.getData().getLast()),
                IntStream::concat));
        return byCountryresults.entrySet().stream()
                .map(e -> new CountryWinRatio(e.getKey(), e.getValue().average().orElse(Double.NaN)))
                .sorted(Comparator.comparingDouble(CountryWinRatio::winRatio).reversed())
                .map(CountryWinRatio::country)
                .toList();
    }

    private record CountryWinRatio(Country country, double winRatio) {
    }
}
