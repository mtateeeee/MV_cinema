package com.example.filmpage.ophim.dto;

import java.util.List;

public record V1ListData(
    String titlePage,
    java.util.List<MovieListItem> items,
    V1ListParams params,
    String type_list
) {}

