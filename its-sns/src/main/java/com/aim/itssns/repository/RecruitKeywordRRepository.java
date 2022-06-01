package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.domain.entity.RecruitKeyword;
import com.aim.itssns.domain.entity.RecruitKeywordR;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitKeywordRRepository extends JpaRepository<RecruitKeywordR, Long> {
    List<RecruitKeywordR> findAllByRecruitKeywordKeywordContent(String userKeywordContent);
    List<RecruitKeywordR> findAllByRecruitKeyword(RecruitKeyword recruitKeyword, Pageable pageable);

}