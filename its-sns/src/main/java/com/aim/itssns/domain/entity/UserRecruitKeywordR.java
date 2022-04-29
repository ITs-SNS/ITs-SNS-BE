package com.aim.itssns.domain.entity;


import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecruitKeywordR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_recruit_keyword_id")
    private Integer userRecruitKeywordId;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name ="keyword_id")
    private RecruitKeyword recruitKeyword;

}
