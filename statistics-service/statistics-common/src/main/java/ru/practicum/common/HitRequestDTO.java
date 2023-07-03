package ru.practicum.common;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class HitRequestDTO {
    private Integer id;
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    private String ip;
    @NotBlank
    private String timestamp;

    @Override
    public String toString() {
        return String.format("Запрос на сохранение статистики. app - %s, uri - %s, ip - %s, timestamp - %s", app, uri, ip, timestamp);
    }
}
