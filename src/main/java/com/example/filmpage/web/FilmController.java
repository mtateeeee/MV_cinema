package com.example.filmpage.web;

import com.example.filmpage.ophim.OPhimClient;
import com.example.filmpage.ophim.dto.EpisodeServerData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FilmController {
  private final OPhimClient ophim;

  public FilmController(OPhimClient ophim) {
    this.ophim = ophim;
  }

  @GetMapping("/film/{slug}")
  public String detail(@PathVariable String slug, Model model) {
    var resp = ophim.movie(slug);
    model.addAttribute("resp", resp);

    String posterUrl = "";
    if (resp != null && resp.movie() != null) {
      var m = resp.movie();
      String p = (m.poster_url() != null && !m.poster_url().isBlank()) ? m.poster_url() : m.thumb_url();
      posterUrl = (p != null && !p.isBlank()) ? ophim.imageUrl(p) : ophim.posterUrlFromSlug(m.slug());
    }
    model.addAttribute("posterUrl", posterUrl != null ? posterUrl : "");

    var rating10 = 0.0;
    if (resp != null && resp.movie() != null) {
      var m = resp.movie();
      if (m.tmdb() != null && m.tmdb().vote_average() != null && m.tmdb().vote_average() > 0) {
        rating10 = m.tmdb().vote_average();
      } else if (m.imdb() != null && m.imdb().vote_average() != null && m.imdb().vote_average() > 0) {
        rating10 = m.imdb().vote_average();
      }
    }
    var rating5 = Math.max(0.0, Math.min(5.0, rating10 / 2.0));
    int fullStars = (int) Math.round(rating5); // keep it simple: 0..5 full stars
    model.addAttribute("rating10", rating10);
    model.addAttribute("ratingStars", fullStars);

    String categorySlug = null;
    if (resp != null && resp.movie() != null && resp.movie().category() != null && !resp.movie().category().isEmpty()) {
      categorySlug = resp.movie().category().get(0).slug();
    }
    model.addAttribute("similarMovies", ophim.similarByCategory(
        resp != null && resp.movie() != null ? resp.movie().slug() : null,
        categorySlug,
        12
    ));

    return "film";
  }

  @GetMapping("/watch/{slug}")
  public String watch(
      @PathVariable String slug,
      @RequestParam(defaultValue = "0") int server,
      @RequestParam(required = false) String ep,
      Model model
  ) {
    var resp = ophim.movie(slug);
    EpisodeServerData selected = null;
    if (resp != null && resp.episodes() != null && !resp.episodes().isEmpty()) {
      var serverIdx = Math.max(0, Math.min(server, resp.episodes().size() - 1));
      var serverData = resp.episodes().get(serverIdx).server_data();
      if (serverData != null && !serverData.isEmpty()) {
        if (ep != null && !ep.isBlank()) {
          selected = serverData.stream().filter(s -> ep.equalsIgnoreCase(s.slug()) || ep.equalsIgnoreCase(s.name())).findFirst().orElse(serverData.get(0));
        } else {
          selected = serverData.get(0);
        }
      }
    }
    model.addAttribute("resp", resp);
    model.addAttribute("serverIndex", server);
    model.addAttribute("epSlug", ep == null ? "" : ep);
    model.addAttribute("selected", selected);
    return "watch";
  }
}

