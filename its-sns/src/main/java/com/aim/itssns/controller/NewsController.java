package com.aim.itssns.controller;


import com.aim.itssns.service.NewsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public void getTrendNewsKeywords()
    {

    }

    public void getNewsList()
    {

    }
}
