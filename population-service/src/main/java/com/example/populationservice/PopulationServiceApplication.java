package com.example.populationservice;

import com.example.populationservice.components.MoviePopulation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class PopulationServiceApplication implements CommandLineRunner {

    private final MoviePopulation moviePopulation;

    public PopulationServiceApplication(MoviePopulation moviePopulation) {
        this.moviePopulation = moviePopulation;
    }

    public static void main(String[] args) {
        SpringApplication.run(PopulationServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        File movies = new File("C:\\Users\\tobit\\IdeaProjects\\movie-recommendation\\population-service\\dataSources\\title.basics.tsv\\data-tail8372702.tsv");
        moviePopulation.refreshData(movies);

    }
}
