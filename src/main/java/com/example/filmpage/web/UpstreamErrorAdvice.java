package com.example.filmpage.web;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class UpstreamErrorAdvice {

  @ExceptionHandler({ResourceAccessException.class})
  public String timeout(ResourceAccessException ex, Model model) {
    model.addAttribute("title", "Upstream timeout");
    model.addAttribute("message", "The film API did not respond in time. Please retry.");
    return "error";
  }

  @ExceptionHandler({HttpClientErrorException.class})
  public String http(HttpClientErrorException ex, Model model) {
    if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
      model.addAttribute("title", "Not found");
      model.addAttribute("message", "We couldn't find that resource.");
      return "error";
    }
    model.addAttribute("title", "Upstream error");
    model.addAttribute("message", "The film API returned an error (" + ex.getStatusCode().value() + ").");
    return "error";
  }

  @ExceptionHandler({RestClientException.class})
  public String generic(RestClientException ex, Model model) {
    model.addAttribute("title", "Request failed");
    model.addAttribute("message", "Something went wrong while contacting the film API.");
    return "error";
  }
}

