package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.NewsTrend;
import com.aim.itssns.domain.entity.RecruitTrend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RecruitTrendRepository extends JpaRepository<RecruitTrend, Long> {
    RecruitTrend findByTrendDate(LocalDate date);
}
