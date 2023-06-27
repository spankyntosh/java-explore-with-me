package ru.practicum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.common.HitRequestDTO;
import ru.practicum.common.StatsResponseDTO;
import ru.practicum.mapper.HitMapper;
import ru.practicum.repository.StatisticsRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsRepository statisticsRepository;

    @Autowired
    public StatisticsServiceImpl(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public HitRequestDTO addHit(HitRequestDTO hitRequestDTO) {
        return HitMapper.toDTO(statisticsRepository.save(HitMapper.toModel(hitRequestDTO)));
    }

    @Override
    public Collection<StatsResponseDTO> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (isNull(uris) || uris.isEmpty()) {
            if (unique) {
                return statisticsRepository.getAllStatisticsWithDistinctIp(start, end);
            } else {
                return statisticsRepository.getAllStatistics(start, end);
            }
        } else if (unique) {
            return statisticsRepository.getStatisticsByUriWithDistinctIp(start, end, uris);
        } else {
            return statisticsRepository.getStatisticsByUri(start, end, uris);
        }
    }
}
