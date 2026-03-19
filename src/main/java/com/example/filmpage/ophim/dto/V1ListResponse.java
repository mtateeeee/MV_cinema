package com.example.filmpage.ophim.dto;

public record V1ListResponse(
    String status,
    String message,
    V1ListData data
) {}

