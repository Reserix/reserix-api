package com.reserix.api.screen.entity;

import com.reserix.api.common.entity.BaseEntity;
import com.reserix.api.theater.entity.Theater;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "rooms",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"theater_id", "name"})
        }
)
public class Room extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: rooms.theater_id -> theaters.id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theater_id", nullable = false)
    private Theater theater;

    @Column(nullable = false)
    private String name;

    @Column(name = "row_count", nullable = false)
    private Integer rowCount;

    @Column(name = "column_count", nullable = false)
    private Integer columnCount;

    protected Room() {
    }

    public Room(Theater theater, String name, Integer rowCount, Integer columnCount) {
        this.theater = theater;
        this.name = name;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
    }
}
