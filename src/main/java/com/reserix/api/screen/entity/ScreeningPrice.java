package com.reserix.api.screen.entity;

import com.reserix.api.common.entity.BaseEntity;
import com.reserix.api.movie.entity.Movie;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "screening_prices",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"screening_id", "seat_type"})
        }
)
public class ScreeningPrice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: screening_prices.screening_id -> screenings.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "screening_id", nullable = false)
    private Screening screening;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @Column(name = "price", nullable = false)
    private Integer price;

    protected ScreeningPrice() {

    }

    public ScreeningPrice(Screening screening, SeatType seatType, Integer price) {
        this.screening = screening;
        this.seatType = seatType;
        this.price = price;
    }
}
