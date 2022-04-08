package com.aim.itssns.domain.entity;


import javax.persistence.*;

@Entity
public class UserKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_keyword_id")
    private Integer userKeywordId;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name ="keyword_id")
    private Keyword keyword;
}
