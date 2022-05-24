package com.aim.itssns.service;

import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.dto.NewsKeywordDto;
import com.aim.itssns.domain.dto.NewsKeywordListDto;
import com.aim.itssns.domain.dto.NewsListDto;
import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.domain.entity.NewsKeywordR;
import com.aim.itssns.repository.NewsKeywordRRepository;
import com.aim.itssns.repository.NewsKeywordRepository;
import com.aim.itssns.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsKeywordRRepository newsKeywordRRepository;
    private final NewsKeywordRepository newsKeywordRepository;

    //디비로부터 lastCrawlingTime을 가져옴 만약 디비에 뉴스정보가 존재하지 않으면 2022/4/16일을 lastCrawlTime으로 함.
    public LocalDateTime findLastNewsCrawlTime() {
        News news = newsRepository.findFirstByOrderByNewsUploadDateDesc();
        if (news == null)
            return LocalDateTime.of(2022, 5, 24, 0, 0, 0);

        return news.getNewsUploadDate();
    }

    @Transactional
    public void saveNewsCrawledDtoList(List<NewsCrawledDto> newsCrawledDtoList) {
        for(NewsCrawledDto newsCrawledDto : newsCrawledDtoList)
            saveNewsAndKeywords(newsCrawledDto);
    }


    public void saveNewsAndKeywords(NewsCrawledDto newsCrawledDto) {
        News news = newsCrawledDto.toEntity();
        //news를 디비에 저장
        newsRepository.save(news);

        //각 뉴스에 해당하는 keyword들과 그 관계를 디비에 저장
        List<NewsKeywordDto> newsKeywordDtoList = newsCrawledDto.getNewsKeywordList();
        for (NewsKeywordDto newsKeywordDto : newsKeywordDtoList) {
            //keyword가 이미 디비에 있는지 확인하고, 만약 없는 경우에만 디비에 저장
            NewsKeyword newsKeyword = newsKeywordDto.toEntity();
            NewsKeyword existedKeyword = newsKeywordRepository.findByKeywordContent(newsKeyword.getKeywordContent());
            if(existedKeyword != null)
                newsKeyword=existedKeyword;
            else
                newsKeywordRepository.save(newsKeyword);
            //keyword와 news의 관계를 디비에 저장
            NewsKeywordR newsKeywordR = NewsKeywordR.builder()
                    .news(news)
                    .newsKeyword(newsKeyword)
                    .build();
            newsKeywordRRepository.save(newsKeywordR);
        }
    }

    public NewsKeywordListDto findTrendNewsKeywords(String dateStr) {
        LocalDate date;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        //string 형태의 날짜를 local date형식으로 변환하고, 만약 변환이 안되는 경우 오늘 날짜로 설정
        try{
            date=LocalDate.parse(dateStr, dateTimeFormatter);
        }catch(Exception e)
        {
            date=LocalDate.now();
        }
        System.out.println(date);
        return new NewsKeywordListDto();
    }

    public NewsListDto findNewsList(String keyword) {
        if(keyword!=null)
        {

        }
        else
        {

        }
        return new NewsListDto();
    }
}
