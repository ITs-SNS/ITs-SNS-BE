package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.Recruit;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class RecruitCrawledDto {
    Integer recruitId;
    String recruitTitle;
    String recruitCompany;
    LocalDateTime recruitStartDate;
    LocalDateTime recruitEndDate;
    List<RecruitKeywordDto> recruitKeywordList;
    String recruitUrl;

    public Recruit toEntity(){
        return Recruit.builder()
                .recruitTitle("공고 제목")
                .recruitCompany("회사 이름")
                .recruitStartDate(LocalDateTime.now())
                .recruitEndDate(LocalDateTime.now())
                .recruitUrl(recruitUrl)
                .build();
    }

}
