package com.example.filmpage.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;

@Configuration
@EnableCaching
public class AppConfig {

  @Bean
  public RestClient restClient(
      @Value("${ophim.request-timeout-ms:8000}") long timeoutMs) {
    var httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(timeoutMs))
        .build();
    var requestFactory = new JdkClientHttpRequestFactory(httpClient);
    requestFactory.setReadTimeout(Duration.ofMillis(timeoutMs));
    return RestClient.builder().requestFactory(requestFactory).build();
  }

  @Bean
  public CacheManager cacheManager(
      @Value("${cache.ttl-seconds.lists:60}") long listsTtlSeconds,
      @Value("${cache.ttl-seconds.movie:300}") long movieTtlSeconds,
      @Value("${cache.ttl-seconds.taxonomy:3600}") long taxonomyTtlSeconds) {
    var lists = new CaffeineCache(
        "lists",
        Caffeine.newBuilder()
            .maximumSize(2_000)
            .recordStats()
            .expireAfterWrite(Duration.ofSeconds(listsTtlSeconds))
            .build());

    var movie = new CaffeineCache(
        "movie",
        Caffeine.newBuilder()
            .maximumSize(2_000)
            .recordStats()
            .expireAfterWrite(Duration.ofSeconds(movieTtlSeconds))
            .build());

    var taxonomy = new CaffeineCache(
        "taxonomy",
        Caffeine.newBuilder()
            .maximumSize(100)
            .recordStats()
            .expireAfterWrite(Duration.ofSeconds(taxonomyTtlSeconds))
            .build());

    var manager = new SimpleCacheManager();
    manager.setCaches(java.util.List.of(lists, movie, taxonomy));
    return manager;
  }
}

