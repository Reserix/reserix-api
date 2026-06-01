package com.reserix.api.movie.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "movie_casts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_movie_casts_movie_actor_role",
                        columnNames = {"movie_id", "actor_name", "role_name"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_movie_casts_movie_id",
                        columnList = "movie_id"
                )
        }
)
public class MovieCast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "movie_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_movie_casts_movie")
    )
    private Movie movie;

    @Column(name = "actor_name", nullable = false)
    private String actorName;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    protected MovieCast() {
    }

    public MovieCast(
            Movie movie,
            String actorName,
            String roleName
    ) {
        this.movie = movie;
        this.actorName = actorName;
        this.roleName = roleName;
    }
}
