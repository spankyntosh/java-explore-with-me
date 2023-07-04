package ru.practicum.statistics;

import ru.practicum.common.HitRequestDTO;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {

    public static HitRequestDTO toHitRequestDTO(String appName, HttpServletRequest servletRequest) {
        return HitRequestDTO.builder()
                .app(appName)
                .uri(servletRequest.getRequestURI())
                .ip(servletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
