package com.reserix.api.screen.entity;

import com.reserix.api.common.entity.BaseEntity;
import com.reserix.api.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "screenings",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"room_id", "start_time"})
        }
)
public class Screening extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: screenings.movie_id -> movies.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    // FK: screenings.room_id -> rooms.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScreeningStatus status;

    protected Screening() {

    }

    public Screening(Movie movie, Room room, LocalDateTime startTime, LocalDateTime endTime) {
        this(movie, room, startTime, endTime, ScreeningStatus.SCHEDULED);
    }

    public Screening(Movie movie, Room room, LocalDateTime startTime, LocalDateTime endTime, ScreeningStatus status) {
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
