package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.NewsTrend;
import com.aim.itssns.domain.entity.RecruitTrend;
import lombok.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class RecruitTrendDto {
    List<RecruitKeywordWithFrequencyDto> recruitKeywordList;
    LocalDate trendDate;
    public RecruitTrend toEntity(){

        String keywordIds = String.join(" ", Arrays.stream(recruitKeywordList.stream().map(RecruitKeywordWithFrequencyDto::getKeywordId).toArray(Integer[]::new)).map(integer -> integer.toString()).toArray(String[]::new));
        String keywordContents = String.join(" ", recruitKeywordList.stream().map(RecruitKeywordWithFrequencyDto::getKeywordContent).toArray(String[]::new));
        String keywordFrequencys = String.join(" ", Arrays.stream(recruitKeywordList.stream().map(RecruitKeywordWithFrequencyDto::getKeywordFrequency).toArray(Integer[]::new)).map(integer -> integer.toString()).toArray(String[]::new));
        return RecruitTrend.builder()
                .keywordIds(keywordIds)
                .keywordContents(keywordContents)
                .keywordFrequencys(keywordFrequencys)
                .trendDate(trendDate)
                .build();
    }
}
