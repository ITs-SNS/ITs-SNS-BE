package com.aim.itssns.repository;

import com.aim.itssns.domain.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitKeywordRepository extends JpaRepository<RecruitKeyword, Long> {
    RecruitKeyword findByKeywordContent(String keywordContent);

}