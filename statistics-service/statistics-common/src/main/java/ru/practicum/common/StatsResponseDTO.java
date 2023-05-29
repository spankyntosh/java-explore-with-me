package ru.practicum.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsResponseDTO {
    private String app;
    private String uri;
    private int hits;
}
