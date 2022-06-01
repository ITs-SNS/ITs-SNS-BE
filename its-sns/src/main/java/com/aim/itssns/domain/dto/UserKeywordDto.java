package com.aim.itssns.domain.dto;


import com.aim.itssns.domain.entity.UserKeyword;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserKeywordDto {

    private Integer userKeywordId;
    private String userEmail;
    //구독하고자 하는 keyword내용
    private String userKeywordContent;
    //recruit or news
    private String userKeywordType;

    public UserKeyword toEntity() {
        return UserKeyword.builder()
                .userEmail(userEmail)
                .userKeywordContent(userKeywordContent)
                .userKeywordType(userKeywordType)
                .build();
    }
}
