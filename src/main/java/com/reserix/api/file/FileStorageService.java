package com.reserix.api.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(MultipartFile file);
}
