package com.aim.itssns.service;

import com.aim.itssns.domain.dto.*;
import com.aim.itssns.domain.entity.*;
import com.aim.itssns.repository.RecruitKeywordRRepository;
import com.aim.itssns.repository.RecruitKeywordRepository;
import com.aim.itssns.repository.RecruitRepository;
import com.aim.itssns.repository.RecruitTrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final RecruitKeywordRepository recruitKeywordRepository;
    private final RecruitKeywordRRepository recruitKeywordRRepository;
    private final RecruitTrendRepository recruitTrendRepository;

    //recruit Repository에서 가장 마지막 recruit에 해당하는 url 가져옴
    public String findLastRecruitUrl()
    {
        Recruit recruit = recruitRepository.findFirstByOrderByRecruitIdDesc();
        if(recruit == null) return "";
        return recruit.getRecruitUrl();
    }

    @Transactional
    public void saveRecruitCrawledDtoList(List<RecruitCrawledDto> recruitCrawledDtoList)
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
            //keyword와 recruit의 관계를 디비에 저장
            RecruitKeywordR recruitKeywordR = RecruitKeywordR.builder()
                    .recruit(recruit)
                    .recruitKeyword(recruitKeyword)
                    .build();
            recruitKeywordRRepository.save(recruitKeywordR);
        }
    }

    public RecruitTrendDto findTrendRecruitKeywords(String dateStr) {
        LocalDate date;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        //string 형태의 날짜를 local date형식으로 변환하고, 만약 변환이 안되는 경우 오늘 날짜로 설정
        try{
            date=LocalDate.parse(dateStr, dateTimeFormatter);
        }catch(Exception e)
        {
            date=LocalDate.now();
        }

        RecruitTrend recruitTrend = recruitTrendRepository.findByTrendDate(date);
        if(recruitTrend != null)
            return recruitTrend.toDto();
        return RecruitTrendDto.builder().build();
    }

    public RecruitListDto findRecruitList(String keyword, Pageable pageable) {
        List<Recruit> recruitList = null;
        //전체 recruit 반환
        if(keyword==null)
        {
            pageable = PageRequest.of(pageable.getPageNumber(), 40, Sort.by("recruitEndDate").descending());
            recruitList = recruitRepository.findAll(pageable).toList();
        }
        //keyword에 해당하는 recruit 반환
        else
        {
            pageable = PageRequest.of(pageable.getPageNumber(), 40, Sort.by("recruitRecruitEndDate").descending());
            RecruitKeyword recruitKeyword = recruitKeywordRepository.findByKeywordContent(keyword);
            List<RecruitKeywordR> recruitKeywordRList= recruitKeywordRRepository.findAllByRecruitKeyword(recruitKeyword, pageable);
            recruitList = recruitKeywordRList.stream().map(RecruitKeywordR::getRecruit).collect(Collectors.toList());
        }

        List<RecruitCrawledDto> recruitCrawledDtoList = recruitList.stream().map(Recruit::toDto).collect(Collectors.toList());

        return RecruitListDto.builder()
                .recruitList(recruitCrawledDtoList)
                .page(pageable.getPageNumber())
                .build();
    }

    @Transactional
    public RecruitTrendDto saveRecruitTrend(LocalDate localDate)
    {
        //해당하는 기간 동안의 recruitCrawledDto 수집
        List<Recruit> recruitList = recruitRepository.findAllByRecruitEndDateGreaterThan(
                LocalDateTime.of(localDate, LocalTime.of(23, 59, 59)));
        List<RecruitCrawledDto> recruitCrawledDtoList = recruitList.stream().map(Recruit::toDto).filter(recruitCrawledDto -> {
            return recruitCrawledDto.getRecruitStartDate().isBefore(LocalDateTime.of(localDate, LocalTime.of(0,0,0)));
        }).collect(Collectors.toList());


        //recruit들을 순회하며 각 키워드들의 빈도대로 정렬
        TreeMap<Integer, RecruitKeywordWithFrequencyDto> recruitKeywordMap = new TreeMap<>();
        for(RecruitCrawledDto recruitCrawledDto : recruitCrawledDtoList)
        {
            for(RecruitKeywordDto recruitKeywordDto : recruitCrawledDto.getRecruitKeywordList())
            {

                if(recruitKeywordMap.containsKey(recruitKeywordDto.getKeywordId()))
                {
                    RecruitKeywordWithFrequencyDto recruitKeywordWithFrequencyDto=recruitKeywordMap.get(recruitKeywordDto.getKeywordId());
                    recruitKeywordWithFrequencyDto.setKeywordFrequency(recruitKeywordWithFrequencyDto.getKeywordFrequency()+1);
                }
                else
                {
                    RecruitKeywordWithFrequencyDto recruitKeywordWithFrequencyDto = RecruitKeywordWithFrequencyDto.builder()
                            .keywordId(recruitKeywordDto.getKeywordId())
                            .keywordContent(recruitKeywordDto.getKeywordContent())
                            .keywordFrequency(1)
                            .build();
                    recruitKeywordMap.put(recruitKeywordDto.getKeywordId(), recruitKeywordWithFrequencyDto);
                }
            }
        }
        List<RecruitKeywordWithFrequencyDto> recruitKeywordList = recruitKeywordMap.values().stream().sorted().collect(Collectors.toList()).subList(0, 20);


        RecruitTrendDto recruitTrendDto = RecruitTrendDto.builder().recruitKeywordList(recruitKeywordList).trendDate(localDate).build();
        //먼저 이미 만들어진 오늘의 trend가 있는지 확인
        RecruitTrend existTrend = recruitTrendRepository.findByTrendDate(localDate);
        if(existTrend!= null)
            recruitTrendRepository.delete(existTrend);

        recruitTrendRepository.save(recruitTrendDto.toEntity());

        return recruitTrendDto;
    }

}
