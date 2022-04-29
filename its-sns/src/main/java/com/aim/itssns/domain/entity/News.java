package com.aim.itssns.domain.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Integer newsId;
    @Column(nullable = false)
    private String newsTitle;
    @Column(length = 15000, nullable = false)
    private String newsContent;
    @Column(nullable = false)
    private String newsSummary;
    @Column(nullable = false)
    private LocalDateTime newsUploadDate;
    private String newsReporter;
    @Column(nullable = false)
    private String newsUrl;

    @OneToMany(mappedBy = "news")
    private List<NewsKeywordR> newsKeywords = new ArrayList<>();
}
