package com.movierecommendation.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    private Long id;
    private String primaryTitle;
    private String originalTitle;
    @Enumerated(EnumType.STRING)
    private Adult adult;
    private String startYear;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Genre> genres;
    @Embedded
    private Rating rating;
}
