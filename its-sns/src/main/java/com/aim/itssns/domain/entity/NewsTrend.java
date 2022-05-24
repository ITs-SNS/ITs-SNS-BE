package com.aim.itssns.domain.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsTrend {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "trend_id",nullable = false)
    private Integer trendId;
    @Column(nullable = false)
    private String keywordIds;
    @Column(nullable = false)
    private String keywordContents;
    @Column(nullable = false)
    private String keywordFrequencys;

    @Column(nullable = false)
    private LocalDate trendDate;
}
