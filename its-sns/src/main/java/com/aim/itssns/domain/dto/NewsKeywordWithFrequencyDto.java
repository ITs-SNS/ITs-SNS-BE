package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.NewsKeyword;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class NewsKeywordWithFrequencyDto implements Comparable<NewsKeywordWithFrequencyDto> {
    private Integer keywordId;
    private String keywordContent;
    private Integer keywordFrequency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsKeywordWithFrequencyDto that = (NewsKeywordWithFrequencyDto) o;
        return keywordId.equals(that.keywordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keywordId);
    }

    @Override
    public int compareTo(NewsKeywordWithFrequencyDto o) {
        if (keywordFrequency < o.keywordFrequency) {
            return 1;
        } else if (keywordFrequency == o.keywordFrequency) {
            return 0;
        } else {
            return -1;
        }

    }
}
