package com.example.filmpage.web;

import com.example.filmpage.ophim.OPhimClient;

/** Helper methods exposed to Thymeleaf templates */
public class TemplateHelpers {
  private final OPhimClient ophim;

  public TemplateHelpers(OPhimClient ophim) {
    this.ophim = ophim;
  }

  /** Get poster URL for list items with fallback from slug. */
  public String listImg(String posterUrl, String thumbUrl, String slug) {
    return ophim.listImageUrl(posterUrl, thumbUrl, slug);
  }
}
