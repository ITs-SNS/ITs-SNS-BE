package com.aim.itssns.domain.dto;

import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.Recruit;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class RecruitCrawledDto {
    String recruitCompany;
    String recruitUrl;
    LocalDateTime recruitStartDate;
    LocalDateTime recruitEndDate;
    String recruitTitle;

    public Recruit toEntity(){
        return Recruit.builder()
                .recruitCompany("가짜")
                .recruitUrl(recruitUrl)
                .recruitStartDate(LocalDateTime.now())
                .recruitEndDate(LocalDateTime.now())
                .recruitTitle("가짜")
                .build();
    }

}
