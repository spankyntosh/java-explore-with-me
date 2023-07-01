package ru.practicum.statistics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatisticsClient;
import ru.practicum.common.HitRequestDTO;
import ru.practicum.common.StatsResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StatisticsService {

    private final StatisticsClient statisticsClient;

    @Autowired
    public StatisticsService(StatisticsClient statisticsClient) {
        this.statisticsClient = statisticsClient;
    }

    public void addHit(HitRequestDTO hitRequestDTO) {
        log.info("Добавление нового просмотра события");
        statisticsClient.saveHit(hitRequestDTO);
    }

    public List<StatsResponseDTO> getStatistics(LocalDateTime start,
                                                LocalDateTime end,
                                                List<String> uris,
                                                Boolean unique) {
        log.info("Получение статистики с параметрами: start {}, end {}, uris {}, unique {}", start, end, uris, unique);
        ResponseEntity<Object> statsServiceResponse = statisticsClient.getStatistics(start, end, uris, unique);
        List<StatsResponseDTO> response = new ObjectMapper().convertValue(statsServiceResponse.getBody(), new TypeReference<>() {});
        return  response;
    }
}
