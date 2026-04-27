package com.reserix.api.theater.service;

import com.reserix.api.theater.dto.TheaterCreateRequest;
import com.reserix.api.theater.dto.TheaterResponse;
import com.reserix.api.theater.entity.Theater;
import com.reserix.api.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterService {
    private final TheaterRepository theaterRepository;

    @Transactional
    public TheaterResponse createTheater(TheaterCreateRequest request) {
        if (theaterRepository.existsByAddressAndName(request.address(), request.name())) {
            throw new IllegalArgumentException("Theater already exists at this address");
        }

        Theater theater = new Theater(
                request.name(),
                request.address()
        );

        Theater savedTheater = theaterRepository.save(theater);

        return TheaterResponse.from(savedTheater);
    }

    public TheaterResponse getTheater(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new IllegalArgumentException("Theater not found"));

        return TheaterResponse.from(theater);
    }

    public List<TheaterResponse> getTheaters() {
        return theaterRepository.findAll()
                .stream()
                .map(TheaterResponse::from)
                .toList();
    }
}
