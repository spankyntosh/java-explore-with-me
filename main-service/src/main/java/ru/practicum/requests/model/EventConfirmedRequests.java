package ru.practicum.requests.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventConfirmedRequests {
    private Integer eventId;
    private Integer confirmedRequests;
}
