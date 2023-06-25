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
import static java.util.Objects.nonNull;

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
        if (isNull(uris) && isNull(unique)) {
            return statisticsRepository.getAllStatistics(start, end);
        }

        if (isNull(uris) && nonNull(unique)) {
            return statisticsRepository.getAllStatisticsWithDistinctIp(start, end);
        }

        if (isNull(unique) && nonNull(uris)) {
            return statisticsRepository.getStatisticsByUri(start, end, uris);
        }

        return statisticsRepository.getStatisticsByUriWithDistinctIp(start, end, uris);
    }
}
