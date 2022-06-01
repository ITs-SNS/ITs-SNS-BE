package com.aim.itssns.controller;

import com.aim.itssns.domain.dto.RecruitTrendDto;
import com.aim.itssns.domain.dto.RecruitListDto;
import com.aim.itssns.service.RecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class RecruitController {
    private final RecruitService recruitService;
    @GetMapping("/recruit_keywords")
    @ResponseStatus(HttpStatus.OK)
    public RecruitTrendDto getTrendRecruitKeywords(@RequestParam(required = false) String date)
    {
        return recruitService.findTrendRecruitKeywords(date);
    }

    @GetMapping("/recruit_list")
    @ResponseStatus(HttpStatus.OK)
    public RecruitListDto getRecruitList(@RequestParam(required = false) String keyword, Pageable pageable)
    {
        return recruitService.findRecruitList(keyword, pageable);
    }


    @GetMapping("/recruit_trend")
    @ResponseStatus(HttpStatus.OK)
    public RecruitTrendDto getTrend()
    {
        return recruitService.saveRecruitTrend(LocalDate.now());
    }
}
