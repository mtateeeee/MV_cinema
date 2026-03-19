package com.example.filmpage.web;

import com.example.filmpage.ophim.OPhimClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BrowseController {
  private final OPhimClient ophim;

  public BrowseController(OPhimClient ophim) {
    this.ophim = ophim;
  }

  @GetMapping("/browse/{listSlug}")
  public String browse(
      @PathVariable String listSlug,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(required = false, name = "genre") String genre,
      @RequestParam(required = false, name = "country") String country,
      @RequestParam(required = false, name = "year") String year,
      @RequestParam(required = false, name = "type") String type,
      @RequestParam(required = false, name = "sortField", defaultValue = "_id") String sortField,
      @RequestParam(required = false, name = "sortType", defaultValue = "desc") String sortType,
      Model model
  ) {
    var resp = ophim.list(listSlug, page, sortField, sortType, genre, country, year, type);
    model.addAttribute("listSlug", listSlug);
    model.addAttribute("resp", resp);
    model.addAttribute("filters", new Filters(genre, country, year, type, sortField, sortType));
    return "browse";
  }

  public record Filters(String genre, String country, String year, String type, String sortField, String sortType) {}
}

