package ru.practicum.mapper;

import ru.practicum.common.HitRequestDTO;
import ru.practicum.model.Statistics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {

    public static Statistics toModel(HitRequestDTO hitRequestDTO) {
        return Statistics.builder()
                .uri(hitRequestDTO.getUri())
                .ip(hitRequestDTO.getIp())
                .appName(hitRequestDTO.getApp())
                .timestamp(LocalDateTime.parse(hitRequestDTO.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public static HitRequestDTO toDTO(Statistics statistics) {
        return HitRequestDTO.builder()
                .id(statistics.getId())
                .uri(statistics.getUri())
                .ip(statistics.getIp())
                .app(statistics.getAppName())
                .timestamp(statistics.getTimestamp().toString())
                .build();
    }
}
