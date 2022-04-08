package com.aim.itssns.domain.entity;


import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Recruit {
    @GeneratedValue(strategy = GenerationType.IDENTITY)  @Id
    @Column(name = "recruit_id")
    private Integer recruitId;
    @Column(nullable = false)
    private String recruitCompany;
    @Column(nullable = false)
    private String recruitUrl;
    @Column(nullable = false)
    private LocalDateTime recruitStartDate;
    @Column(nullable = false)
    private LocalDateTime recruitEndDate;
    @Column(nullable = false)
    private String recruitTitle;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitKeyword> recruitKeywordList = new ArrayList<>();
}
