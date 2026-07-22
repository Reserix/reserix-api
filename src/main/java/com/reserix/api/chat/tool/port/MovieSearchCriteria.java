package com.reserix.api.chat.tool.port;
import java.time.LocalDate;
public record MovieSearchCriteria(String keyword, String genre, LocalDate date) { }
