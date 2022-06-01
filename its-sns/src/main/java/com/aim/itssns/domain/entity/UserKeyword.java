package com.aim.itssns.domain.entity;


import com.aim.itssns.domain.dto.UserKeywordDto;
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
public class UserKeyword {
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Id
    @Column(name = "user_keyword_id",nullable = false)
    private Integer userKeywordId;
    @Column(nullable = false)
    private String userEmail;
    //구독하고자 하는 keyword내용
    @Column(nullable = false)
    private String userKeywordContent;
    //recruit or news
    @Column(nullable = false)
    private String userKeywordType;

    public UserKeywordDto toDto() {
        return UserKeywordDto.builder()
                .userKeywordId(userKeywordId)
                .userEmail(userEmail)
                .userKeywordContent(userKeywordContent)
                .userKeywordType(userKeywordType)
                .build();
    }
}
