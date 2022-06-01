package com.aim.itssns.domain.entity;


import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.dto.NewsKeywordDto;
import com.aim.itssns.domain.dto.RecruitCrawledDto;
import com.aim.itssns.domain.dto.RecruitKeywordDto;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recruit {
    @GeneratedValue(strategy = GenerationType.IDENTITY)  @Id
    @Column(name = "recruit_id")
    private Integer recruitId;
    @Column(nullable = false)
    private String recruitCompany;
    @Column(nullable = false)
    private String recruitUrl;
    @Column(nullable = false)
    private LocalDateTime recruitStartDate;
    @Column(nullable = false)
    private LocalDateTime recruitEndDate;
    @Column(nullable = false)
    private LocalDate recruitCrawlDate;
    @Column(nullable = false)
    private String recruitTitle;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitKeywordR> recruitKeywords = new ArrayList<>();

    public RecruitCrawledDto toDto(){
        List<RecruitKeywordDto> recruitKeywordDtoList = recruitKeywords.stream().map(recruitKeywordR ->
                recruitKeywordR.getRecruitKeyword().toDto()).collect(Collectors.toList());

        return RecruitCrawledDto
                .builder()
                .recruitId(recruitId)
                .recruitTitle(recruitTitle)
                .recruitCompany(recruitCompany)
                .recruitStartDate(recruitStartDate)
                .recruitEndDate(recruitEndDate)
                .recruitCrawlDate(recruitCrawlDate)
                .recruitKeywordList(recruitKeywordDtoList)
                .recruitUrl(recruitUrl)
                .build();
    }
}
