package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.News;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class NewsCrawledDto {
    String newsTitle;
    String newsContent;
    LocalDateTime newsUploadDate;
    String newsReporter;
    String newsUrl;


    public News toEntity(){
        return News.builder()
                .newsTitle("newsTitle")
                .newsContent("가짜")
                .newsSummary("가짜")
                .newsUploadDate(newsUploadDate)
                .newsReporter("가짜")
                .newsUrl(newsUrl)
                .build();
    }

}
