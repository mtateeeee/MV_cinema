package com.example.filmpage.ophim.dto;

import java.util.List;

public record SearchData(
    String titlePage,
    List<MovieListItem> items,
    SearchParams params
) {}

