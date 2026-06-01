package com.reserix.api.file;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService{
    @Value("${app.storage.upload-dir}")
    private String uploadDir;

    @Override
    public String upload(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String extension =
                    StringUtils.getFilenameExtension(
                            file.getOriginalFilename()
                    );

            String fileName =
                    UUID.randomUUID() + "." + extension;

            Path filePath =
                    uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath
            );

            return "/uploads/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to upload file",
                    e
            );
        }
    }
}
