package com.reserix.api.theater.service;

import com.reserix.api.common.exception.BusinessException;
import com.reserix.api.common.exception.ErrorCode;
import com.reserix.api.common.response.PageResponse;
import com.reserix.api.file.FileStorageService;
import com.reserix.api.theater.dto.TheaterCreateRequest;
import com.reserix.api.theater.dto.TheaterResponse;
import com.reserix.api.theater.entity.Theater;
import com.reserix.api.theater.repository.TheaterRepository;
import com.reserix.api.user.dto.UserResponse;
import com.reserix.api.user.entity.User;
import com.reserix.api.user.entity.UserRole;
import com.reserix.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterService {
    private final TheaterRepository theaterRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public TheaterResponse createTheater(TheaterCreateRequest request, List<MultipartFile> thumbnails) {
        if (theaterRepository.existsByAddressAndName(request.address(), request.name())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Theater already exists at this address");
        }

        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Theater owner not found"));

        if (owner.getRole() != UserRole.THEATER_MANAGER) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "User is not a theater owner");
        }

        Theater theater = new Theater(
                request.name(),
                request.address(),
                owner,
                request.description(),
                request.phoneNumber(),
                request.email(),
                request.latitude(),
                request.longitude(),
                request.status()
        );

        addThumbnails(theater, thumbnails);

        Theater savedTheater = theaterRepository.save(theater);

        return TheaterResponse.from(savedTheater);
    }

    public TheaterResponse getTheater(Long theaterId) {
        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() -> new IllegalArgumentException("Theater not found"));

        return TheaterResponse.from(theater);
    }

    public PageResponse<TheaterResponse> getTheaters(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        Page<Theater> theaterPage = theaterRepository.findAll(pageable);

        List<TheaterResponse> content = theaterPage.getContent()
                .stream()
                .map(TheaterResponse::from)
                .toList();

        return new PageResponse<>(
                content,
                theaterPage.getNumber(),
                theaterPage.getSize(),
                theaterPage.getTotalElements(),
                theaterPage.getTotalPages()
        );
    }

    private void addThumbnails(Theater theater, List<MultipartFile> thumbnails) {
        if (thumbnails == null || thumbnails.isEmpty()) {
            return;
        }

        int sortOrder = 0;

        for (MultipartFile thumbnail : thumbnails) {
            if (thumbnail == null || thumbnail.isEmpty()) {
                continue;
            }

            String imageUrl = fileStorageService.upload(thumbnail);
            theater.addThumbnail(imageUrl, sortOrder, sortOrder == 0);

            sortOrder++;
        }
    }
}
