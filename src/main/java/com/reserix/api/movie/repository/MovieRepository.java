package com.reserix.api.movie.repository;

import com.reserix.api.movie.entity.Movie;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("""
            SELECT m
            FROM Movie m
            WHERE
                :keyword IS NULL
                OR :keyword = ''
                OR LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(m.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY m.title ASC
            """)
    List<Movie> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
