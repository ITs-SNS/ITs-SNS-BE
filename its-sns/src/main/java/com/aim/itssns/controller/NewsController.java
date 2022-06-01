package com.aim.itssns.controller;

import com.aim.itssns.domain.dto.NewsListDto;
import com.aim.itssns.domain.dto.NewsTrendDto;
import com.aim.itssns.service.NewsService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/news_keywords")
    @ResponseStatus(HttpStatus.OK)
    public NewsTrendDto getTrendNewsKeywords(@RequestParam(required = false) String date)
    {
        return newsService.findTrendNewsKeywords(date);
    }

    @GetMapping("/news_list")
    @ResponseStatus(HttpStatus.OK)
    public NewsListDto getNewsList(@RequestParam(required = false) String keyword, Pageable pageable)
    {
        return newsService.findNewsList(keyword, pageable);
    }
}
