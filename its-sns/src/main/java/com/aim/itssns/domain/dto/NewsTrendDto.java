package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.NewsTrend;
import lombok.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
@ToString
public class NewsTrendDto {
    List<NewsKeywordWithFrequencyDto> newsKeywordList;
    LocalDate trendDate;
    public NewsTrend toEntity(){

        String keywordIds = String.join(" ", Arrays.stream(newsKeywordList.stream().map(NewsKeywordWithFrequencyDto::getKeywordId).toArray(Integer[]::new)).map(integer -> integer.toString()).toArray(String[]::new));
        String keywordContents = String.join(" ", newsKeywordList.stream().map(NewsKeywordWithFrequencyDto::getKeywordContent).toArray(String[]::new));
        String keywordFrequencys = String.join(" ", Arrays.stream(newsKeywordList.stream().map(NewsKeywordWithFrequencyDto::getKeywordFrequency).toArray(Integer[]::new)).map(integer -> integer.toString()).toArray(String[]::new));
        return NewsTrend.builder()
                .keywordIds(keywordIds)
                .keywordContents(keywordContents)
                .keywordFrequencys(keywordFrequencys)
                .trendDate(trendDate)
                .build();
    }
}
