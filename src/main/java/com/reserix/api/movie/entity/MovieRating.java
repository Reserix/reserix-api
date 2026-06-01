package com.reserix.api.movie.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import com.reserix.api.common.entity.BaseEntity;
import com.reserix.api.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "movie_ratings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_movie_ratings_movie_user",
                        columnNames = {"movie_id", "user_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_movie_ratings_movie_id",
                        columnList = "movie_id"
                ),
                @Index(
                        name = "idx_movie_ratings_user_id",
                        columnList = "user_id"
                )
        }
)
public class MovieRating extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "movie_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_movie_ratings_movie")
    )
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_movie_ratings_user")
    )
    private User user;

    @Min(1)
    @Max(10)
    @Column(nullable = false)
    private Integer score;

    protected MovieRating() {
    }

    public MovieRating(
            Movie movie,
            User user,
            Integer score
    ) {
        this.movie = movie;
        this.user = user;
        this.score = score;
    }

    public void updateScore(Integer score) {
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException(
                    "Score must be between 1 and 5"
            );
        }

        this.score = score;
    }
}
