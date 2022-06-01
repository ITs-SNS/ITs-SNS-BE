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


    public News toEntity() {

        return News.builder()
                .newsTitle(newsTitle)
                .newsSummary(newsSummary)
                .newsUploadDate(newsUploadDate)
                .newsReporter(newsReporter)
                .newsUrl(newsUrl)
                .build();
    }

    public String mailContent() {
        String str = "뉴스 제목 : " + newsTitle + "\n";
        str += "요약 : " + newsSummary + "\n";
        str += "키워드 : ";
        for (NewsKeywordDto newsKeywordDto : newsKeywordList)
            str += "#" + newsKeywordDto.getKeywordContent() + " ";
        str += "\n";
        str += "업로드 날짜 : " + newsUploadDate.toString()+"\n";
        str += "URL : " + newsUrl + "\n\n";
        return str;
    }
}
