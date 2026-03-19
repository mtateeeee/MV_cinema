package com.example.filmpage.web;

import com.example.filmpage.ophim.OPhimClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {
  private final OPhimClient ophim;

  public SearchController(OPhimClient ophim) {
    this.ophim = ophim;
  }

  @GetMapping("/search")
  public String search(
      @RequestParam(name = "q", required = false) String keyword,
      @RequestParam(defaultValue = "1") int page,
      Model model
  ) {
    if (keyword == null) keyword = "";
    var resp = ophim.search(keyword, page);
    model.addAttribute("keyword", keyword);
    model.addAttribute("resp", resp);
    model.addAttribute("page", page);
    int totalPages = 1;
    if (resp != null && resp.data() != null && resp.data().params() != null && resp.data().params().pagination() != null) {
      var p = resp.data().params().pagination();
      if (p.totalItemsPerPage() > 0) {
        totalPages = (int) Math.ceil((double) p.totalItems() / (double) p.totalItemsPerPage());
      }
    }
    model.addAttribute("totalPages", totalPages);
    return "search";
  }
}

