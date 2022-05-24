package com.aim.itssns.domain.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class NewsKeywordListDto {
    List<NewsKeywordWithFrequencyDto> newsKeywordList;
}
