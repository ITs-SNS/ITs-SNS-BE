package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit, Long> {
    Recruit findFirstByOrderByRecruitIdDesc();

    List<Recruit> findAllByRecruitEndDateGreaterThan(LocalDateTime of);
}