package com.aim.itssns.domain.entity;

import javax.persistence.*;

@Entity
public class RecruitKeyword {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_keyword_id")
    private Integer recruitKeywordId;


    @ManyToOne
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    @ManyToOne
    @JoinColumn(name ="keyword_id")
    private Keyword keyword;

}
