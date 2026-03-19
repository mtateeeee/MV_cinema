package com.example.filmpage.web;

import com.example.filmpage.ophim.OPhimClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
  private final OPhimClient ophim;

  public HomeController(OPhimClient ophim) {
    this.ophim = ophim;
  }

  @GetMapping("/")
  public String home(@RequestParam(defaultValue = "1") int page, Model model) {
    var latest = ophim.list("phim-moi-cap-nhat", page, "_id", "desc", null, null, null, null);
    model.addAttribute("latest", latest);
    model.addAttribute("recommendations", ophim.topRatedRecommendations(10));
    return "home";
  }
}

