package com.movierecommendation.backend.service;

import com.movierecommendation.backend.model.Movie;
import com.movierecommendation.backend.repository.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Page<Movie> getMovieList(String title, String genre, Pageable pageable) {
        if(title == null && genre == null)
            return movieRepository.findAll(pageable);
        if(title == null)
            return movieRepository.findAllByGenres(genre, pageable);
        if(genre == null)
            return movieRepository.findAllByOriginalTitle(title, pageable);
        return movieRepository.findAllByOriginalTitleAndGenres(title, genre, pageable);
    }

    public Movie getMovie(Long id) {
        Optional<Movie> op = movieRepository.findById(id);
        if(op.isPresent())
            return op.get();
        else throw new EntityNotFoundException("No se encontro la pelicula con id: " + id);
    }
}
