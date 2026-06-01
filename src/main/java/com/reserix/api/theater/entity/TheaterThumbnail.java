package com.reserix.api.theater.entity;

import com.reserix.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "theater_thumbnails",
        indexes = {
                @Index(name = "idx_theater_thumbnails_theater_id", columnList = "theater_id")
        }
)
public class TheaterThumbnail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "theater_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_theater_thumbnails_theater")
    )
    private Theater theater;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_primary", nullable = false)
    private Boolean primaryThumbnail;

    protected TheaterThumbnail() {
    }

    public TheaterThumbnail(
            Theater theater,
            String imageUrl,
            Integer sortOrder,
            Boolean primaryThumbnail
    ) {
        this.theater = theater;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.primaryThumbnail = primaryThumbnail;
    }

    public String getFilePath() {
        return this.imageUrl;
    }
}
