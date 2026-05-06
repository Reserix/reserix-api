package com.reserix.api.screen.entity;

import com.reserix.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import javax.net.ssl.SSLSession;

@Getter
@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"room_id", "row_number", "column_number"})
        }
)
public class Seat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: seats.room_id -> rooms.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "row_number")
    private Integer rowNumber;

    @Column(name = "column_number")
    private Integer columnNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    protected Seat() {

    }

    public Seat(Room room, Integer rowNumber, Integer columnNumber) {
        this.room = room;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.seatType = SeatType.STANDARD;
    }
}
