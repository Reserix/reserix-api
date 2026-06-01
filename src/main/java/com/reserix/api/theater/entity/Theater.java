package com.reserix.api.theater.entity;

import com.reserix.api.common.entity.BaseEntity;
import com.reserix.api.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_theaters_owner")
    )
    private User owner;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TheaterStatus status;

    @OneToMany(
            mappedBy = "theater",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sortOrder ASC, id ASC")
    private List<TheaterThumbnail> thumbnails = new ArrayList<>();

    protected Theater() {
    }

    public Theater(
            String name,
            String address,
            User owner,
            String description,
            String phoneNumber,
            String email,
            Double latitude,
            Double longitude,
            TheaterStatus status) {
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public void addThumbnail(String imageUrl, Integer sortOrder, boolean primary) {
        TheaterThumbnail thumbnail = new TheaterThumbnail(this, imageUrl, sortOrder, primary);
        this.thumbnails.add(thumbnail);
    }

    public void clearThumbnails() {
        this.thumbnails.clear();
    }
}
