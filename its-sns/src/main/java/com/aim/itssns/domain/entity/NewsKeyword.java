package com.aim.itssns.domain.entity;

import javax.persistence.*;

@Entity
public class NewsKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_keyword_id")
    private Integer newsKeywordId;

    @ManyToOne
    @JoinColumn(name="news_id")
    private News news;

    @ManyToOne
    @JoinColumn(name="keyword_id")
    private Keyword keyword;
}
