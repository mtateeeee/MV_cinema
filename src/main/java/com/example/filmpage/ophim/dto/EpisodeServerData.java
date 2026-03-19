package com.example.filmpage.ophim.dto;

public record EpisodeServerData(
    String name,
    String slug,
    String filename,
    String link_embed,
    String link_m3u8
) {}

