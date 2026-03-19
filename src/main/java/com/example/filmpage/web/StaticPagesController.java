package com.example.filmpage.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticPagesController {

  @GetMapping("/login")
  public String login(Model model) {
    model.addAttribute("title", "Đăng nhập");
    model.addAttribute("message", "Chức năng đang được phát triển.");
    return "simple-page";
  }

  @GetMapping("/register")
  public String register(Model model) {
    model.addAttribute("title", "Đăng ký");
    model.addAttribute("message", "Chức năng đang được phát triển.");
    return "simple-page";
  }

  @GetMapping("/bookmark")
  public String bookmark(Model model) {
    model.addAttribute("title", "Bookmark");
    model.addAttribute("message", "Chức năng đang được phát triển.");
    return "simple-page";
  }
}

