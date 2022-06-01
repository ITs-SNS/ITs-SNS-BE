package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.News;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    News findFirstByOrderByNewsUploadDateDesc();

    List<News> findAllByNewsUploadDateBetween(LocalDateTime from, LocalDateTime to);
}
