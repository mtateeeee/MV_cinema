package com.example.filmpage.ophim.dto;

/** One movie in the "ĐỀ XUẤT PHIM" sidebar, with rating for sorting/display */
public record RecommendationItem(
    String slug,
    String name,
    String originName,
    Integer year,
    String posterUrl,
    String thumbUrl,
    double rating
) {}
