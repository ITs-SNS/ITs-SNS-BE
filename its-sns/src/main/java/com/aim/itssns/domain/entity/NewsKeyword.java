package com.aim.itssns.domain.entity;

import com.aim.itssns.domain.dto.NewsKeywordDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsKeyword {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Integer keywordId;
    @Column(unique = true,nullable = false)
    private String keywordContent;

    @OneToMany(mappedBy = "newsKeyword")
    private List<NewsKeywordR> newsKeywordList = new ArrayList<>();

    public NewsKeywordDto toDto() {
        return NewsKeywordDto.builder()
                .keywordId(keywordId)
                .keywordContent(keywordContent)
                .build();
    }
}
