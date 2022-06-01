package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.NewsTrend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsTrendRepository extends JpaRepository<NewsTrend, Long> {
    NewsTrend findByTrendDate(LocalDate date);
}
