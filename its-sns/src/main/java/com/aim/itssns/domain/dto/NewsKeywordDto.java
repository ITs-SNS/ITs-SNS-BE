package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.domain.entity.NewsKeywordR;
import com.aim.itssns.domain.entity.UserNewsKeywordR;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class NewsKeywordDto {
    private Integer keywordId;
    private String keywordContent;
    public NewsKeyword toEntity(){

        return NewsKeyword.builder()
                .keywordContent(keywordContent)
                .build();
    }
}
