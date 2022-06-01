package com.aim.itssns.domain.entity;

import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.dto.NewsKeywordDto;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
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
public class News {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Integer newsId;
    @Column(nullable = false)
    private String newsTitle;
    @Column(nullable = false)
    private String newsSummary;
    @Column(nullable = false)
    private LocalDateTime newsUploadDate;
    private String newsReporter;
    @Column(nullable = false)
    private String newsUrl;

    @OneToMany(mappedBy = "news")
    private List<NewsKeywordR> newsKeywords = new ArrayList<>();

    public NewsCrawledDto toDto(){
        List<NewsKeywordDto> newsKeywordDtoList = newsKeywords.stream().map(newsKeywordR ->
                newsKeywordR.getNewsKeyword().toDto()).collect(Collectors.toList());

        return NewsCrawledDto
                .builder()
                .newsId(newsId)
                .newsTitle(newsTitle)
                .newsSummary(newsSummary)
                .newsUploadDate(newsUploadDate)
                .newsReporter(newsReporter)
                .newsKeywordList(newsKeywordDtoList)
                .newsUrl(newsUrl)
                .build();
    }
}
