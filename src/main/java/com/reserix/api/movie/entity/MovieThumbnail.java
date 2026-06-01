package com.reserix.api.movie.entity;

import com.reserix.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "movie_thumbnails",
        indexes = {
                @Index(name = "idx_movie_thumbnails_movie_id", columnList = "movie_id")
        }
)
public class MovieThumbnail extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "movie_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_movie_thumbnails_movie")
    )
    private Movie movie;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_primary", nullable = false)
    private Boolean primaryThumbnail;

    protected MovieThumbnail() {
    }

    public MovieThumbnail(
            Movie movie,
            String imageUrl,
            Integer sortOrder,
            Boolean primaryThumbnail
    ) {
        this.movie = movie;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.primaryThumbnail = primaryThumbnail;
    }

    public String getFilePath() {
        return this.imageUrl;
    }
}
