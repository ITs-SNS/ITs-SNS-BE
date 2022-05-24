package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.NewsKeyword;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class NewsKeywordWithFrequencyDto {
    private Integer keywordId;
    private String keywordContent;
    private Integer keywordFrequency;
}
