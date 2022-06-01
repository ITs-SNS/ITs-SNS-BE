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
                .recruitTitle(recruitTitle)
                .recruitCompany(recruitCompany)
                .recruitStartDate(recruitStartDate)
                .recruitEndDate(recruitEndDate)
                .recruitUrl(recruitUrl)
                .build();
    }

    public String mailContent() {
        String str = "채용공고 제목 : " + recruitTitle + "\n";
        str += "회사명 : "+recruitCompany + "\n";
        str += "키워드 : ";
        for(RecruitKeywordDto recruitKeywordDto: recruitKeywordList)
            str += "#"+ recruitKeywordDto.getKeywordContent()+" ";
        str+="\n";
        str += "시작 날짜 : " + recruitStartDate.toString()+"\n";
        str += "종료 날짜 : " + recruitEndDate.toString()+"\n";
        str += "URL : " + recruitUrl +"\n\n";
        return str;
    }
}
