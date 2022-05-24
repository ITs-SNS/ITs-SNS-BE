package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.NewsKeyword;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class NewsCrawledDto {
    Integer newsId;
    String newsTitle;
    String newsSummary;
    LocalDateTime newsUploadDate;
    String newsReporter;
    List<NewsKeywordDto> newsKeywordList;
    String newsUrl;


    public News toEntity(){

        //TODO: 해당 내용 채워넣기
        return News.builder()
                .newsTitle("뉴스 제목")
                .newsSummary("뉴스 요약")
                .newsUploadDate(newsUploadDate)
                .newsReporter("뉴스 기자")
                .newsUrl(newsUrl)
                .build();
    }

}
