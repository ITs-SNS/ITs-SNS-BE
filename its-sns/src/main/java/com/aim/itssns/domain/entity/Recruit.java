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
    @Column
    private LocalDateTime recruitEndDate;
    @Column(nullable = false)
    private String recruitTitle;

    @OneToMany(mappedBy = "recruit")
    private List<RecruitKeywordR> recruitKeywordList = new ArrayList<>();
}
