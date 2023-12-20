package com.movierecommendation.backend.controller;

import com.movierecommendation.backend.model.Movie;
import com.movierecommendation.backend.service.MovieService;
import com.movierecommendation.backend.thread.RefreshData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movies")
@CrossOrigin(origins = "http://localhost:3000")
public class MovieController {
    private final MovieService movieService;
    private final RefreshData refreshData;

    public MovieController(MovieService movieService, RefreshData refreshData) {
        this.movieService = movieService;
        this.refreshData = refreshData;
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

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshData(){
        refreshData.refreshData();
        return ResponseEntity.ok("Se actualizaron los datos");
    }
}
