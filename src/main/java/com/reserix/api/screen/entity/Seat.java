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
                @UniqueConstraint(columnNames = {"room_id", "row_number", "column_number"}),
                @UniqueConstraint(columnNames = {"room_id", "seat_label"})
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

    @Column(name = "seat_label", nullable = false, length = 20)
    private String seatLabel; // A1, A2, B1

    @Column(name = "row_label", nullable = false, length = 10)
    private String rowLabel; // A, B, C

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber; // The seat number in a row

    @Column(name = "active", nullable = false)
    private Boolean active; // if the seat is real or not.

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    protected Seat() {

    }

    public Seat(
            Room room,
            Integer rowNumber,
            Integer columnNumber,
            String rowLabel,
            Integer seatNumber,
            SeatType seatType,
            Boolean active,
            Integer displayOrder
    ) {
        this.room = room;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.seatLabel = rowLabel + seatNumber;
        this.seatType = seatType == null ? SeatType.STANDARD : seatType;
        this.active = active == null ? true : active;
        this.displayOrder = displayOrder == null ? 0 : displayOrder;
    }
}
