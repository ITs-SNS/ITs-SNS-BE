package com.aim.itssns.service;

import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.dto.RecruitCrawledDto;
import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.Recruit;
import com.aim.itssns.repository.NewsRepository;
import com.aim.itssns.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;

    //news Repository에서 시간 가져옴, 만약 디비에 뉴스정보가 존재하지 않으면 2022/4/16일을 lastCrawlTime으로 함.
    public String findLastRecruitUrl()
    {
        Recruit recruit = recruitRepository.findLastByOrderByRecruitId();
        if(recruit == null) return "";
        return recruit.getRecruitUrl();
    }

    public void saveRecruitCrawledList(List<RecruitCrawledDto> recruitCrawledDtoList)
    {
        List<Recruit> recruitList =  recruitCrawledDtoList.stream().map(recruitCrawledDto -> recruitCrawledDto.toEntity()).collect(Collectors.toList());
        recruitRepository.saveAll(recruitList);
    }

}
