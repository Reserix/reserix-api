package com.reserix.api.theater.entity;

import com.reserix.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "theaters",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"address", "name"})
        }
)
public class Theater extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    protected Theater() {
    }

    public Theater(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
