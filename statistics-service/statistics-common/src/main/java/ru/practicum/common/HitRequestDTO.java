package ru.practicum.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class HitRequestDTO {
    private Integer id;
    @NotNull
    private String app;
    @NotNull
    private String uri;
    @NotNull
    private String ip;
    private String timestamp;
}
