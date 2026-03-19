package com.example.filmpage.web;

import com.example.filmpage.ophim.OPhimClient;
import com.example.filmpage.ophim.dto.TaxonomyItem;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@FunctionalInterface
interface Function3<A, B, C, R> {
  R apply(A a, B b, C c);
}

@ControllerAdvice
public class GlobalModelAdvice {
  private final OPhimClient ophim;

  public GlobalModelAdvice(OPhimClient ophim) {
    this.ophim = ophim;
  }

  @ModelAttribute("genres")
  public List<TaxonomyItem> genres() {
    return ophim.genres();
  }

  @ModelAttribute("countries")
  public List<TaxonomyItem> countries() {
    return ophim.countries();
  }

  @ModelAttribute("img")
  public java.util.function.Function<String, String> img() {
    return ophim::imageUrl;
  }

  @ModelAttribute("imgFallback")
  public Function3<String, String, String, String> imgFallback() {
    return ophim::posterWithFallback;
  }

  @ModelAttribute
  public void common(Model model) {
    model.addAttribute("lists", List.of(
        new NavLink("Phim mới cập nhật", "/browse/phim-moi-cap-nhat"),
        new NavLink("Phim lẻ", "/browse/phim-le"),
        new NavLink("Phim bộ", "/browse/phim-bo"),
        new NavLink("TV Shows", "/browse/tv-shows"),
        new NavLink("Hoạt hình", "/browse/hoat-hinh"),
        new NavLink("Sắp chiếu", "/browse/phim-sap-chieu")
    ));
  }

  public record NavLink(String label, String href) {}
}

