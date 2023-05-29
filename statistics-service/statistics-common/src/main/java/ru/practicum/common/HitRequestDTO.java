package ru.practicum.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class HitRequestDTO {
    Integer id;
    @NotNull
    String appId;
    @NotNull
    String uri;
    @NotNull
    String ip;
    @PastOrPresent
    @DateTimeFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    LocalDateTime timestamp;
}
