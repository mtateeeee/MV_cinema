package com.example.filmpage.ophim.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

public record MovieListItem(
    String _id,
    String name,
    String slug,
    String origin_name,
    @JsonAlias("thumb") String thumb_url,
    @JsonAlias("poster") String poster_url,
    Integer year,
    String type,
    String time,
    String episode_current,
    String quality,
    String lang,
    List<TaxonomyItem> category,
    List<TaxonomyItem> country
) {}

