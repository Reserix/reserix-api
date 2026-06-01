package com.reserix.api.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ThumbnailUploadHelper {

    private final FileStorageService fileStorageService;

    public void addThumbnails(
            List<MultipartFile> thumbnails,
            ThumbnailAppender appender
    ) {
        if (thumbnails == null || thumbnails.isEmpty()) {
            return;
        }

        int sortOrder = 0;

        for (MultipartFile thumbnail : thumbnails) {
            if (thumbnail == null || thumbnail.isEmpty()) {
                continue;
            }

            String imageUrl = fileStorageService.upload(thumbnail);
            appender.add(imageUrl, sortOrder, sortOrder == 0);

            sortOrder++;
        }
    }

    @FunctionalInterface
    public interface ThumbnailAppender {
        void add(String imageUrl, int sortOrder, boolean isPrimary);
    }
}