package ru.practicum.mapper;

import ru.practicum.common.HitRequestDTO;
import ru.practicum.model.Statistics;

public class HitMapper {

    public static Statistics toModel(HitRequestDTO hitRequestDTO) {
        return Statistics.builder()
                .uri(hitRequestDTO.getUri())
                .ip(hitRequestDTO.getIp())
                .appName(hitRequestDTO.getAppId())
                .timestamp(hitRequestDTO.getTimestamp())
                .build();
    }

    public static HitRequestDTO toDTO(Statistics statistics) {
        return HitRequestDTO.builder()
                .id(statistics.getId())
                .uri(statistics.getUri())
                .ip(statistics.getIp())
                .appId(statistics.getAppName())
                .timestamp(statistics.getTimestamp())
                .build();
    }
}
