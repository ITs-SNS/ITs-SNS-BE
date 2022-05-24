package com.aim.itssns.domain.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class RecruitListDto {
    List<RecruitCrawledDto> recruitList;
    Integer page;
}
