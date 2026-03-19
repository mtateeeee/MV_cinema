package com.example.filmpage.ophim.dto;

public record TmdbInfo(
    String type,
    String id,
    Integer season,
    Double vote_average,
    Integer vote_count
) {}

