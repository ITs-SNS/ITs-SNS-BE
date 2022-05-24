package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.domain.entity.RecruitKeyword;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class RecruitKeywordDto {
    private Integer keywordId;
    private String keywordContent;

    public RecruitKeyword toEntity(){

        return RecruitKeyword.builder()
                .keywordContent(keywordContent)
                .build();
    }
}
