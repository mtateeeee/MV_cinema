package com.example.filmpage.ophim.dto;

public record SearchResponse(
    String status,
    String message,
    SearchData data
) {}

