package com.aim.itssns.domain.entity;


import com.aim.itssns.domain.dto.NewsKeywordWithFrequencyDto;
import com.aim.itssns.domain.dto.NewsTrendDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsTrend {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "trend_id",nullable = false)
    private Integer trendId;
    @Column(nullable = false)
    private String keywordIds;
    @Column(nullable = false)
    private String keywordContents;
    @Column(nullable = false)
    private String keywordFrequencys;

    @Column(nullable = false)
    private LocalDate trendDate;

    public NewsTrendDto toDto(){

        List<NewsKeywordWithFrequencyDto> newsKeywordWithFrequencyDtoList = new ArrayList<>();

        List<String> keywordIdList = Arrays.asList(keywordIds.split(" "));
        List<String> keywordContentList = Arrays.asList(keywordContents.split(" "));
        List<String> keywordFrequencyList = Arrays.asList(keywordFrequencys.split(" "));

        for(int i=0; i<keywordIdList.size(); i++)
        {
            newsKeywordWithFrequencyDtoList.add(NewsKeywordWithFrequencyDto.builder()
                    .keywordId(Integer.valueOf(keywordIdList.get(i)))
                    .keywordContent(keywordContentList.get(i))
                    .keywordFrequency(Integer.valueOf(keywordFrequencyList.get(i)))
                    .build());
        }

        return NewsTrendDto.builder()
                .newsKeywordList(newsKeywordWithFrequencyDtoList)
                .trendDate(trendDate)
                .build();
    }
}
