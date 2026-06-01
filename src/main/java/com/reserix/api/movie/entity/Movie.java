package com.reserix.api.movie.entity;

import com.reserix.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(
        name = "movies"
)
public class Movie extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "director", nullable = false)
    private String director;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;

    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MovieCast> casts = new ArrayList<>();

    @OneToMany(mappedBy = "movie")
    private List<MovieRating> ratings = new ArrayList<>();

    protected Movie() {

    }

    public Movie(
            String title,
            String description,
            Integer durationMinutes,
            String thumbnailUrl,
            String director
    ) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.thumbnailUrl = thumbnailUrl;
        this.director = director;
        this.averageRating = BigDecimal.ZERO;
        this.ratingCount = 0;
    }

    public void updateRating(
            BigDecimal averageRating,
            Integer ratingCount
    ) {
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }
}
