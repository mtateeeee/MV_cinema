package com.example.filmpage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FilmPageApplication {
  public static void main(String[] args) {
    SpringApplication.run(FilmPageApplication.class, args);
  }
}

