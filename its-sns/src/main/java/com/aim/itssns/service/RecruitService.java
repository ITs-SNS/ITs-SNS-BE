package com.aim.itssns.service;

import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.dto.NewsKeywordDto;
import com.aim.itssns.domain.dto.RecruitCrawledDto;
import com.aim.itssns.domain.dto.RecruitKeywordDto;
import com.aim.itssns.domain.entity.*;
import com.aim.itssns.repository.RecruitKeywordRRepository;
import com.aim.itssns.repository.RecruitKeywordRepository;
import com.aim.itssns.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final RecruitKeywordRepository recruitKeywordRepository;
    private final RecruitKeywordRRepository recruitKeywordRRepository;

    //recruit Repository에서 가장 마지막 recruit에 해당하는 url 가져옴
    public String findLastRecruitUrl()
    {
        Recruit recruit = recruitRepository.findLastByOrderByRecruitId();
        if(recruit == null) return "";
        return recruit.getRecruitUrl();
    }

    //TODO :save RecruitCrawldDtoList로 이름 변경하기.
    @Transactional
    public void saveRecruitCrawledList(List<RecruitCrawledDto> recruitCrawledDtoList)
    {
        for(RecruitCrawledDto recruitCrawledDto : recruitCrawledDtoList)
            saveRecruitAndKeywords(recruitCrawledDto);
    }


    public void saveRecruitAndKeywords(RecruitCrawledDto recruitCrawledDto) {
        Recruit recruit = recruitCrawledDto.toEntity();
        //채용공고를 디비에 저장
        recruitRepository.save(recruit);

        //각 채용공고에 해당하는 keyword들과 그 관계를 디비에 저장
        List<RecruitKeywordDto> recruitKeywordDtoList = recruitCrawledDto.getRecruitKeywordList();
        for (RecruitKeywordDto recruitKeywordDto : recruitKeywordDtoList) {
            //keyword가 이미 디비에 있는지 확인하고, 만약 없는 경우에만 디비에 저장
            RecruitKeyword recruitKeyword = recruitKeywordDto.toEntity();
            RecruitKeyword existedKeyword = recruitKeywordRepository.findByKeywordContent(recruitKeyword.getKeywordContent());
            if(existedKeyword != null)
                recruitKeyword=existedKeyword;
            else
                recruitKeywordRepository.save(recruitKeyword);
            //keyword와 news의 관계를 디비에 저장
            RecruitKeywordR recruitKeywordR = RecruitKeywordR.builder()
                    .recruit(recruit)
                    .recruitKeyword(recruitKeyword)
                    .build();
            recruitKeywordRRepository.save(recruitKeywordR);
        }
    }

}
