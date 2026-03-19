package com.example.filmpage.ophim.dto;

import java.util.List;

public record EpisodeServer(
    String server_name,
    boolean is_ai,
    java.util.List<EpisodeServerData> server_data
) {}

