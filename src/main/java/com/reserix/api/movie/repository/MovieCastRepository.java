package com.reserix.api.movie.repository;

import com.reserix.api.movie.entity.MovieCast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCastRepository extends JpaRepository<MovieCast, Long> {
}
