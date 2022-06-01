package com.aim.itssns.domain.entity;

import com.aim.itssns.domain.dto.NewsKeywordDto;
import com.aim.itssns.domain.dto.RecruitKeywordDto;
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
public class RecruitKeyword {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Integer keywordId;
    @Column(unique = true,nullable = false)
    private String keywordContent;

    @OneToMany(mappedBy = "recruitKeyword")
    private List<RecruitKeywordR> recruitKeywordList = new ArrayList<>();

    public RecruitKeywordDto toDto() {
        return RecruitKeywordDto.builder()
                .keywordId(keywordId)
                .keywordContent(keywordContent)
                .build();
    }
}
