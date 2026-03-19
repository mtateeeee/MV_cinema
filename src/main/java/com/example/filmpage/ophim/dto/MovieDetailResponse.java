package com.example.filmpage.ophim.dto;

import java.util.List;

public record MovieDetailResponse(
    boolean status,
    String msg,
    MovieDetail movie,
    List<EpisodeServer> episodes
) {}

