package com.example.filmpage.ophim.dto;

public record Pagination(
    int totalItems,
    int totalItemsPerPage,
    int currentPage,
    int totalPages
) {}

