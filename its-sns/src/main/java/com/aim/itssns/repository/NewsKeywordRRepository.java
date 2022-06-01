package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.domain.entity.NewsKeywordR;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsKeywordRRepository extends JpaRepository<NewsKeywordR, Long> {
    List<NewsKeywordR> findAllByNewsKeyword(NewsKeyword newsKeyword, Pageable pageable);
    List<NewsKeywordR> findAllByNewsKeywordKeywordContentAndNewsNewsUploadDateBetween(String keywordContent, LocalDateTime start, LocalDateTime end);
}