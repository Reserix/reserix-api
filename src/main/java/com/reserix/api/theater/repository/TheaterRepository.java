package com.reserix.api.theater.repository;

import com.reserix.api.theater.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
    boolean existsByAddressAndName(String address, String name);
}
