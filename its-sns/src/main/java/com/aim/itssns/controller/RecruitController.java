package com.aim.itssns.controller;

import com.aim.itssns.domain.dto.NewsKeywordListDto;
import com.aim.itssns.domain.dto.NewsListDto;
import com.aim.itssns.domain.dto.RecruitKeywordListDto;
import com.aim.itssns.domain.dto.RecruitListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecruitController {
    @GetMapping("/recruit_keywords")
    @ResponseStatus(HttpStatus.OK)
    public RecruitKeywordListDto getTrendRecruitKeywords()
    {
        return new RecruitKeywordListDto();
    }

    @GetMapping("/recruit_list")
    @ResponseStatus(HttpStatus.OK)
    public RecruitListDto getRecruitList()
    {
        return new RecruitListDto();
    }
}
