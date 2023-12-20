package com.movierecommendation.backend.thread;

import com.movierecommendation.backend.model.Adult;
import com.movierecommendation.backend.model.Genre;
import com.movierecommendation.backend.model.Movie;
import com.movierecommendation.backend.model.Rating;
import com.movierecommendation.backend.repository.GenreRepository;
import com.movierecommendation.backend.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Component
public class RefreshData {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final File movies;
    private final File ratings;
    private static final Logger logger = LoggerFactory.getLogger(RefreshData.class);
    @Autowired
    public RefreshData(MovieRepository movieRepository, GenreRepository genreRepository, File movies, File ratings){
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.movies = movies;
        this.ratings = ratings;
    }

    public void refreshData(){
        try(
                BufferedReader moviesReader = Files.newBufferedReader(movies.toPath());
                BufferedReader ratingsReader = Files.newBufferedReader(ratings.toPath())
        ){
            moviesReader.readLine();
            ratingsReader.readLine();
            logger.info("[x] Iniciado proceso de carga de datos");
            int maxRecordSaved = 1000;
            HashMap<Long, Movie> moviesWithoutRating = new HashMap<>();
            HashMap<Long, Rating> ratingWithoutMovie = new HashMap<>();
            HashMap<String, Genre> actualGenre = populateGenre();
            while(moviesReader.ready() || ratingsReader.ready()) {
                List<Movie> moviesReady = new LinkedList<>();
                String line;
                int moviesCount = 0;
                while ((line = moviesReader.readLine()) != null && moviesCount < maxRecordSaved) {
                    String[] lineItems = line.split("\t");
                    Long id = Long.parseLong(lineItems[0].substring(2));


                    String[] genre = lineItems[8].split(",");
                    List<Genre> genres = new LinkedList<>();
                    for (String g : genre) {
                        if (actualGenre.containsKey(g)) {
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
                            .primaryTitle(lineItems[2])
                            .originalTitle(lineItems[3])
                            .startYear(lineItems[5])
                            .adult((Boolean.parseBoolean(lineItems[4])) ? Adult.YES : Adult.NO)
                            .genres(genres)
                            .build();

                    if (ratingWithoutMovie.containsKey(id)) {
                        m.setRating(ratingWithoutMovie.remove(id));
                        moviesReady.add(m);
                    } else {
                        moviesWithoutRating.put(id, m);
                    }

                    moviesCount++;
                }
                int ratingsCount = 0;
                while ((line = ratingsReader.readLine()) != null && ratingsCount < maxRecordSaved) {
                    String[] lineItems = line.split("\t");
                    Long id = Long.parseLong(lineItems[0].substring(2));
                    Rating r = Rating.builder()
                            .averageRating(Double.parseDouble(lineItems[1]))
                            .numVotes(Integer.parseInt(lineItems[2]))
                            .build();

                    if (moviesWithoutRating.containsKey(id)) {
                        Movie m = moviesWithoutRating.remove(id);
                        m.setRating(r);
                        moviesReady.add(m);
                    } else {
                        ratingWithoutMovie.put(id, r);
                    }
                    ratingsCount++;
                }
                movieRepository.saveAll(moviesReady);
                logger.info("""
                    Se cargaron los datos correctamente
                    Peliculas cargadas: {} / En base de datos: {}
                    Peliculas sin rating: {}
                    Generos cargados: {}
                    """, moviesReady.size(), movieRepository.count(),
                        moviesWithoutRating.size(),
                        actualGenre.size()
                );
            }
            int moviesCount = 0;
            List<Movie> moviesReady = new LinkedList<>();
            for(Movie m : moviesWithoutRating.values()){
                if(moviesCount >= maxRecordSaved){
                    movieRepository.saveAll(moviesReady);
                    moviesReady.clear();
                    moviesCount = 0;
                }
                moviesReady.add(m);
                moviesCount++;
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

    private HashMap<String, Genre> populateGenre() {
        HashMap<String, Genre> actualGenre = new HashMap<>();
        genreRepository.findAll().forEach(g -> actualGenre.put(g.getName(), g));
        return actualGenre;
    }
}
