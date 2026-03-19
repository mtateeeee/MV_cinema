package com.example.filmpage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ophim")
public record OPhimProperties(
    String baseUrl,
    String imageBaseUrl,
    long requestTimeoutMs
) {}

