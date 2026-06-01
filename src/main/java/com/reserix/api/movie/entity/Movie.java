package com.reserix.api.movie.entity;

import com.reserix.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Column(name = "genre")
    private String genre;

    @Column(name = "language")
    private String language;

    @Column(name = "minimum_age")
    private Integer minimumAge;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sortOrder ASC, id ASC")
    private List<MovieThumbnail> thumbnails = new ArrayList<>();

    protected Movie() {

    }

    public Movie(
            String title,
            String description,
            Integer durationMinutes,
            String director,
            String genre,
            String language,
            Integer minimumAge,
            LocalDate releaseDate,
            String trailerUrl
    ) {
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.director = director;
        this.genre = genre;
        this.language = language;
        this.minimumAge = minimumAge;
        this.releaseDate = releaseDate;
        this.trailerUrl = trailerUrl;
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

    public void addThumbnail(String imageUrl, Integer sortOrder, boolean primary) {
        MovieThumbnail thumbnail = new MovieThumbnail(this, imageUrl, sortOrder, primary);
        this.thumbnails.add(thumbnail);
    }

    public void clearThumbnails() {
        this.thumbnails.clear();
    }
}
