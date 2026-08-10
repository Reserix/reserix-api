package com.reserix.api.chat.tool.port;

public record MovieSearchItem(
        Long movieId,
        String title,
        Integer durationMinutes,
        String description) { }
