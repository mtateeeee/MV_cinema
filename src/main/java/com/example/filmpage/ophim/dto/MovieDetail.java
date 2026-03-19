package com.example.filmpage.ophim.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MovieDetail(
    String _id,
    TmdbInfo tmdb,
    ImdbInfo imdb,
    String name,
    String slug,
    String origin_name,
    String content,
    String type,
    String status,
    @JsonProperty("thumb_url") @JsonAlias("thumb") String thumb_url,
    @JsonProperty("poster_url") @JsonAlias("poster") String poster_url,
    String trailer_url,
    String time,
    String episode_current,
    String episode_total,
    String quality,
    String lang,
    Integer year,
    List<String> actor,
    List<String> director,
    List<TaxonomyItem> category,
    List<TaxonomyItem> country
) {}

