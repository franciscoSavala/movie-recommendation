package com.example.populationservice.repository;

import com.example.populationservice.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query(value = "SELECT m.id from movie m", nativeQuery = true)
    List<Long> getAllId();
}
