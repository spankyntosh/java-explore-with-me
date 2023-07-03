package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.HitRequestDTO;
import ru.practicum.common.StatsResponseDTO;
import ru.practicum.service.StatisticsService;

import javax.validation.Valid;
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
        this.statisticsService = statisticsService;
    }

    @PostMapping("/hit")
    public HitRequestDTO addHit(@Valid @RequestBody HitRequestDTO hitRequestDTO) {
        log.info("Пришёл запрос на сохранение информации по обращению к эндпоинту. {}", hitRequestDTO);
        return statisticsService.addHit(hitRequestDTO);
    }

    @GetMapping("/stats")
    public Collection<StatsResponseDTO> getStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                      @RequestParam(required = false) List<String> uris,
                                                      @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Пришёл запрос на получение статистики по посещениям. uris - {}, start - {}, end - {}", uris, start, end);
        Collection<StatsResponseDTO> response = statisticsService.getStatistics(start, end, uris, unique);
        System.out.println("Ответ вопрошателю_______________");
        System.out.println(response);

        return response;
    }
}
