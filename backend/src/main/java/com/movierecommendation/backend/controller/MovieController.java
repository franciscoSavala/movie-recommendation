package com.movierecommendation.backend.controller;

import com.movierecommendation.backend.model.Movie;
import com.movierecommendation.backend.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin("*")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<Page<Movie>> getMovies(Pageable pageable,
                                                @RequestParam(required = false) String title,
                                                @RequestParam(required = false) String genre) {
        if(pageable.getPageSize() > 100) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(movieService.getMovieList(title, genre, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id){
        return ResponseEntity.ok(movieService.getMovie(id));
    }

}
