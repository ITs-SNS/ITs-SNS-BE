package com.aim.itssns.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class RecruitKeywordWithFrequencyDto {
    private Integer keywordId;
    private String keywordContent;
    private Integer keywordFrequency;
}
