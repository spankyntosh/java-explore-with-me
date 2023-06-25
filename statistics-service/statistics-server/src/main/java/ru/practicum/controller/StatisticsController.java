package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.HitRequestDTO;
import ru.practicum.common.StatsResponseDTO;
import ru.practicum.service.StatisticsService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@Validated
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        log.info("Пришёл запрос на сохранение информации по обращению к эндпоинту");
        this.statisticsService = statisticsService;
    }

    @PostMapping("/hit")
    public HitRequestDTO addHit(@RequestBody HitRequestDTO hitRequestDTO) {
        return statisticsService.addHit(hitRequestDTO);
    }

    @GetMapping("/stats")
    public Collection<StatsResponseDTO> getStatistics(@RequestParam LocalDateTime start,
                                                      @RequestParam LocalDateTime end,
                                                      @RequestParam(required = false) List<String> uris,
                                                      @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Пришёл запрос на получение статистики по посещениям");
        return statisticsService.getStatistics(start, end, uris, unique);
    }
}
