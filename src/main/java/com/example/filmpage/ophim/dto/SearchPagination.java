package com.example.filmpage.ophim.dto;

public record SearchPagination(
    int totalItems,
    int totalItemsPerPage,
    int currentPage,
    int pageRanges
) {}

