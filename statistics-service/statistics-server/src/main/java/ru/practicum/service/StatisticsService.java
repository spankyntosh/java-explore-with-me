package ru.practicum.service;

import ru.practicum.common.HitRequestDTO;
import ru.practicum.common.StatsResponseDTO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatisticsService {

    HitRequestDTO addHit(HitRequestDTO hitRequestDTO);

    Collection<StatsResponseDTO> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
