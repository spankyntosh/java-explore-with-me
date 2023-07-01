package ru.practicum.events.model;

import lombok.*;
import ru.practicum.categories.model.Category;
import ru.practicum.events.enums.EventState;
import ru.practicum.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "")
    private String annotation;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
    @Column(name = "")
    private LocalDateTime createdOn;
    @Column(name = "")
    private String description;
    @Column(name = "")
    private LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User initiator;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;
    @Column(name = "")
    private Boolean paid;
    @Column(name = "")
    private Integer participantLimit;
    @Column(name = "")
    private LocalDateTime publishedOn;
    @Column(name = "")
    private Boolean requestModeration;
    @Column(name = "")
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(name = "")
    private String title;
}
