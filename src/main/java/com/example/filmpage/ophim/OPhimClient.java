package com.example.filmpage.ophim;

import com.example.filmpage.config.OPhimProperties;
import com.example.filmpage.ophim.dto.MovieDetail;
import com.example.filmpage.ophim.dto.MovieDetailResponse;
import com.example.filmpage.ophim.dto.MovieListItem;
import com.example.filmpage.ophim.dto.MovieListResponse;
import com.example.filmpage.ophim.dto.Pagination;
import com.example.filmpage.ophim.dto.RecommendationItem;
import com.example.filmpage.ophim.dto.SearchResponse;
import com.example.filmpage.ophim.dto.TaxonomyItem;
import com.example.filmpage.ophim.dto.V1ListResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class OPhimClient {
  private final RestClient http;
  private final OPhimProperties props;

  public OPhimClient(RestClient http, OPhimProperties props) {
    this.http = http;
    this.props = props;
  }

  @Cacheable(cacheNames = "taxonomy", key = "'genres'")
  public List<TaxonomyItem> genres() {
    return http.get()
        .uri(props.baseUrl() + "/the-loai")
        .retrieve()
        .body(new org.springframework.core.ParameterizedTypeReference<List<TaxonomyItem>>() {});
  }

  @Cacheable(cacheNames = "taxonomy", key = "'countries'")
  public List<TaxonomyItem> countries() {
    return http.get()
        .uri(props.baseUrl() + "/quoc-gia")
        .retrieve()
        .body(new org.springframework.core.ParameterizedTypeReference<List<TaxonomyItem>>() {});
  }

  @Cacheable(cacheNames = "lists", key = "T(java.lang.String).format('list:%s:%d:%s:%s:%s:%s:%s', #listSlug, #page, #sortField, #sortType, #genre, #country, #year, #type)")
  public MovieListResponse list(
      String listSlug,
      int page,
      String sortField,
      String sortType,
      String genre,
      String country,
      String year,
      String type
  ) {
    var uri = UriComponentsBuilder
        .fromHttpUrl(props.baseUrl() + "/v1/api/danh-sach/" + listSlug)
        .queryParam("page", Math.max(1, page))
        .queryParamIfPresent("sort_field", opt(sortField))
        .queryParamIfPresent("sort_type", opt(sortType))
        .queryParamIfPresent("category", opt(genre))
        .queryParamIfPresent("country", opt(country))
        .queryParamIfPresent("year", opt(year))
        .queryParamIfPresent("type", opt(type))
        .build()
        .encode()
        .toUri();

    var v1 = http.get().uri(uri).retrieve().body(V1ListResponse.class);
    return toMovieListResponse(v1);
  }

  /** No cache so poster/thumb URLs from API are always fresh. */
  public MovieDetailResponse movie(String slug) {
    return http.get()
        .uri(props.baseUrl() + "/phim/" + slug)
        .retrieve()
        .body(MovieDetailResponse.class);
  }

  /** Top 10 movies by rating (tmdb/imdb), cached. Fetches list then details for first items. */
  @Cacheable(cacheNames = "lists", key = "'recommendations'")
  public List<RecommendationItem> topRatedRecommendations(int limit) {
    MovieListResponse listRes = list("phim-moi-cap-nhat", 1, null, null, null, null, null, null);
    if (listRes == null || !listRes.status() || listRes.items() == null || listRes.items().isEmpty()) {
      return List.of();
    }
    int take = Math.min(20, listRes.items().size());
    List<RecommendationItem> withRating = new ArrayList<>();
    for (int i = 0; i < take; i++) {
      MovieListItem item = listRes.items().get(i);
      String slug = item.slug();
      if (slug == null || slug.isBlank()) continue;
      try {
        MovieDetailResponse dr = movie(slug);
        if (dr == null || !dr.status() || dr.movie() == null) continue;
        MovieDetail m = dr.movie();
        double rating10 = 0.0;
        if (m.tmdb() != null && m.tmdb().vote_average() != null && m.tmdb().vote_average() > 0) {
          rating10 = m.tmdb().vote_average();
        } else if (m.imdb() != null && m.imdb().vote_average() != null && m.imdb().vote_average() > 0) {
          rating10 = m.imdb().vote_average();
        }
        String poster = listImageUrl(m.poster_url(), m.thumb_url(), m.slug());
        String thumb = (m.thumb_url() != null && !m.thumb_url().isBlank()) ? imageUrl(m.thumb_url()) : posterUrlFromSlug(m.slug());
        withRating.add(new RecommendationItem(
            m.slug(),
            m.name(),
            m.origin_name(),
            m.year(),
            poster,
            thumb,
            rating10
        ));
      } catch (Exception e) {
        // Skip this movie on error (e.g. 404, timeout)
      }
    }
    return withRating.stream()
        .sorted(Comparator.comparingDouble(RecommendationItem::rating).reversed())
        .limit(limit)
        .toList();
  }

  /** Phim tương tự (cùng thể loại), loại trừ phim hiện tại, dùng cho "CÓ THỂ BẠN SẼ THÍCH". */
  @Cacheable(cacheNames = "lists", key = "T(java.lang.String).format('similar:%s:%s', #excludeSlug, #categorySlug)")
  public List<MovieListItem> similarByCategory(String excludeSlug, String categorySlug, int limit) {
    MovieListResponse listRes = list(
        "phim-moi-cap-nhat",
        1,
        null,
        null,
        categorySlug,
        null,
        null,
        null
    );
    if (listRes == null || !listRes.status() || listRes.items() == null || listRes.items().isEmpty()) {
      return List.of();
    }
    String excl = (excludeSlug != null && !excludeSlug.isBlank()) ? excludeSlug : "";
    return listRes.items().stream()
        .filter(it -> it.slug() != null && !it.slug().equalsIgnoreCase(excl))
        .limit(limit)
        .toList();
  }

  @Cacheable(cacheNames = "lists", key = "T(java.lang.String).format('search:%s:%d', #keyword, #page)")
  public SearchResponse search(String keyword, int page) {
    var uri = UriComponentsBuilder
        .fromHttpUrl(props.baseUrl() + "/v1/api/tim-kiem")
        .queryParam("keyword", keyword == null ? "" : keyword)
        .queryParam("page", Math.max(1, page))
        .build()
        .encode()
        .toUri();
    return http.get().uri(uri).retrieve().body(SearchResponse.class);
  }

  private MovieListResponse toMovieListResponse(V1ListResponse v1) {
    if (v1 == null || v1.data() == null) {
      return new MovieListResponse(false, java.util.List.of(), props.imageBaseUrl(), new Pagination(0, 24, 1, 1));
    }
    var data = v1.data();
    var items = data.items() == null ? java.util.List.<com.example.filmpage.ophim.dto.MovieListItem>of() : data.items();
    int totalItems = 0;
    int perPage = 24;
    int currentPage = 1;
    int totalPages = 1;
    if (data.params() != null && data.params().pagination() != null) {
      var p = data.params().pagination();
      totalItems = Math.max(0, p.totalItems());
      perPage = p.totalItemsPerPage() > 0 ? p.totalItemsPerPage() : perPage;
      currentPage = p.currentPage() > 0 ? p.currentPage() : currentPage;
      totalPages = perPage > 0 ? (int) Math.ceil((double) totalItems / (double) perPage) : 1;
      if (totalPages < 1) totalPages = 1;
    }
    return new MovieListResponse("success".equalsIgnoreCase(v1.status()), items, props.imageBaseUrl(), new Pagination(totalItems, perPage, currentPage, totalPages));
  }

  public String imageUrl(String maybeRelativeThumbOrPoster) {
    if (maybeRelativeThumbOrPoster == null || maybeRelativeThumbOrPoster.isBlank()) return "";
    if (maybeRelativeThumbOrPoster.startsWith("http://") || maybeRelativeThumbOrPoster.startsWith("https://")) {
      return maybeRelativeThumbOrPoster;
    }
    return props.imageBaseUrl() + maybeRelativeThumbOrPoster;
  }

  /** Get poster URL with fallback chain: poster -> thumb -> slug-based */
  public String posterWithFallback(String posterUrl, String thumbUrl, String slug) {
    String p = (posterUrl != null && !posterUrl.isBlank()) ? posterUrl : thumbUrl;
    if (p != null && !p.isBlank()) {
      return imageUrl(p);
    }
    // Both blank, try slug-based fallback
    return posterUrlFromSlug(slug);
  }

  /** Fallback when API does not return poster/thumb: build URL from slug (e.g. slug-poster.jpg). */
  public String posterUrlFromSlug(String slug) {
    if (slug == null || slug.isBlank()) return "";
    String base = props.imageBaseUrl();
    if (!base.endsWith("/")) base = base + "/";
    return base + slug + "-poster.jpg";
  }

  public String thumbUrlFromSlug(String slug) {
    if (slug == null || slug.isBlank()) return "";
    String base = props.imageBaseUrl();
    if (!base.endsWith("/")) base = base + "/";
    return base + slug + "-thumb.jpg";
  }

  /** Get poster URL for list items with fallback from slug. */
  public String listImageUrl(String posterUrl, String thumbUrl, String slug) {
    String p = (posterUrl != null && !posterUrl.isBlank()) ? posterUrl : thumbUrl;
    if (p != null && !p.isBlank()) {
      return imageUrl(p);
    }
    // Fallback to slug-based URL
    return posterUrlFromSlug(slug);
  }

  private static java.util.Optional<String> opt(String value) {
    return (value == null || value.isBlank()) ? java.util.Optional.empty() : java.util.Optional.of(value);
  }
}

