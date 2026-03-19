package com.example.filmpage.ophim.dto;

import java.util.List;

public record MovieListResponse(
    boolean status,
    List<MovieListItem> items,
    String pathImage,
    Pagination pagination
) {}

