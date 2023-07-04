package ru.practicum.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class ErrorResponse {

    private String status;
    private String reason;
    private String message;
    private String timestamp;

}
