package com.movierecommendation.backend.repository;

import com.movierecommendation.backend.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
