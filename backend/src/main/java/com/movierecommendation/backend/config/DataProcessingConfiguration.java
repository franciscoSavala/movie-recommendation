package com.movierecommendation.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class DataProcessingConfiguration {
    @Bean
    public File movies(){
        return new File("C:\\Users\\tobit\\IdeaProjects\\movie-recommendation\\backend\\dataSources\\title.basics.tsv\\data.tsv");
    }

    @Bean
    public File ratings(){
        return new File("C:\\Users\\tobit\\IdeaProjects\\movie-recommendation\\backend\\dataSources\\title.ratings.tsv\\data.tsv");
    }

}
