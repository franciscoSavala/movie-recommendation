package com.movierecommendation.backend.repository;

import com.movierecommendation.backend.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findAllByOriginalTitleAndGenres(String title, String genre, Pageable pageable);

    Page<Movie> findAllByOriginalTitle(String title, Pageable pageable);
    Page<Movie> findAllByGenres(String genre, Pageable pageable);
}
