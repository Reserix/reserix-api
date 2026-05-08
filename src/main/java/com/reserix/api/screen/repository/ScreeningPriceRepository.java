package com.reserix.api.screen.repository;

import com.reserix.api.screen.entity.ScreeningPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreeningPriceRepository extends JpaRepository<ScreeningPrice, Long> {
    Optional<ScreeningPrice> findByScreeningId(Long screeningId);
}
