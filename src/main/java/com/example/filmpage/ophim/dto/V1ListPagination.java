package com.example.filmpage.ophim.dto;

public record V1ListPagination(
    int totalItems,
    int totalItemsPerPage,
    int currentPage,
    int pageRanges
) {}

