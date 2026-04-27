package com.reserix.api.user.entity;

import com.reserix.api.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"email"})
        },
        indexes = {
                @Index(name = "idx_users_status", columnList = "status")
        }
)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private Integer role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
