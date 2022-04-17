package com.aim.itssns.domain.entity;

import com.sun.istack.NotNull;
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
public class Keyword {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Integer keywordId;
    @Column(unique = true,nullable = false)
    private String keywordContent;

    @OneToMany(mappedBy = "keyword")
    private List<NewsKeyword> newsKeywordList = new ArrayList<>();

    @OneToMany(mappedBy = "keyword")
    private List<RecruitKeyword> recruitKeywordList = new ArrayList<>();

    @OneToMany(mappedBy = "keyword")
    private List<UserKeyword> userKeywordList = new ArrayList<>();
}
