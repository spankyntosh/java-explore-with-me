package ru.practicum.requests.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventConfirmedRequests {
    private Integer eventId;
    private Integer confirmedRequests;

    public EventConfirmedRequests(Integer eventId, Long confirmedRequests) {
        this.eventId = eventId;
        this.confirmedRequests = Integer.parseInt(confirmedRequests.toString());
    }
}
