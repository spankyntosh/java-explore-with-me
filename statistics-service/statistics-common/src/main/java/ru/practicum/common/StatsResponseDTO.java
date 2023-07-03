package ru.practicum.common;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class StatsResponseDTO {
    private String app;
    private String uri;
    private long hits;

    @Override
    public String toString() {
        return String.format("Статистика. uri - %s, hits - %d", uri, hits);
    }
}
