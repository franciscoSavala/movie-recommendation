package com.example.populationservice.components;

import com.example.populationservice.model.Adult;
import com.example.populationservice.model.Genre;
import com.example.populationservice.model.Movie;
import com.example.populationservice.repository.GenreRepository;
import com.example.populationservice.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MoviePopulation implements Populate{
    private final File movies = new File("/Users/fsavala/IdeaProjects/movie-recommendation/population-service/dataSources/title.basics.tsv/data.tsv");
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private static final Logger logger = LoggerFactory.getLogger(MoviePopulation.class);
    @Autowired
    public MoviePopulation(MovieRepository movieRepository,
                           GenreRepository genreRepository){
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }

    public void populate(){
        try(
                BufferedReader moviesReader = Files.newBufferedReader(movies.toPath())
        ){
            moviesReader.readLine();
            logger.info("[x] Iniciado proceso de carga de datos");
            Thread.ofVirtual().start(() -> {
               while(true){
                   try {
                       Thread.sleep(5000);
                       logger.info("""
                               Peliculas cargadas: {}
                               """, movieRepository.count());
                   } catch (InterruptedException e) {
                       throw new RuntimeException(e);
                   }
               }
            });
            int maxRecordSaved = 1000;
            HashMap<String, Genre> actualGenre = populateGenre();
            Set<Long> ids = populateIds();
            while(moviesReader.ready()) {
                List<Movie> moviesReady = new LinkedList<>();
                String line;
                int moviesCount = 0;
                while ((line = moviesReader.readLine()) != null && moviesCount < maxRecordSaved) {
                    String[] lineItems = line.split("\t");
                    Long id = Long.parseLong(lineItems[0].substring(2));
                    if(ids.contains(id)) continue;
                    ids.add(id);
                    String[] genre = lineItems[8].split(",");
                    List<Genre> genres = new LinkedList<>();

                    for (String g : genre) {
                        if (actualGenre.containsKey(g)) { //4854657
                            genres.add(actualGenre.get(g));
                        } else {
                            Genre toBeAdded = Genre.builder()
                                    .name(g)
                                    .build();
                            actualGenre.put(g, toBeAdded);
                            genres.add(toBeAdded);
                        }
                    }
                    Movie m = Movie.builder()
                            .id(id)
                            .primaryTitle(lineItems[2].length() > 255 ? lineItems[2].substring(0,255) : lineItems[2])
                            .originalTitle(lineItems[3].length() > 255 ? lineItems[3].substring(0,255) : lineItems[3])
                            .startYear(lineItems[5])
                            .adult((Boolean.parseBoolean(lineItems[4])) ? Adult.YES : Adult.NO)
                            .genres(genres)
                            .build();

                    moviesReady.add(m);

                    moviesCount++;
                }

                Thread.ofVirtual().start(() -> {
                    movieRepository.saveAll(moviesReady);
                });
            }
            logger.info("[x] Finalizado proceso de carga de datos");

        } catch (FileNotFoundException e) {
            logger.error("No se encontró el archivo");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Fallo de I/O al leer archivo");
            throw new RuntimeException(e);
        }
    }

    private Set<Long> populateIds() {
        return new HashSet<>(movieRepository.getAllId());
    }

    private HashMap<String, Genre> populateGenre() {
        HashMap<String, Genre> actualGenre = new HashMap<>();
        genreRepository.findAll().forEach(g -> actualGenre.put(g.getName(), g));
        return actualGenre;
    }

}
