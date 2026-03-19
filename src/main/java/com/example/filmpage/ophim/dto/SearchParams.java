package com.example.filmpage.ophim.dto;

public record SearchParams(
    String keyword,
    SearchPagination pagination
) {}

