package com.aim.itssns.service;

import com.aim.itssns.domain.dto.*;
import com.aim.itssns.domain.entity.News;
import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.domain.entity.NewsKeywordR;
import com.aim.itssns.domain.entity.NewsTrend;
import com.aim.itssns.repository.NewsKeywordRRepository;
import com.aim.itssns.repository.NewsKeywordRepository;
import com.aim.itssns.repository.NewsRepository;
import com.aim.itssns.repository.NewsTrendRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsKeywordRRepository newsKeywordRRepository;
    private final NewsKeywordRepository newsKeywordRepository;
    private final NewsTrendRepository newsTrendRepository;

    //디비로부터 lastCrawlingTime을 가져옴 만약 디비에 뉴스정보가 존재하지 않으면 당일 00시를 lastCrawlTime으로 함.
    public LocalDateTime findLastNewsCrawlTime() {
        News news = newsRepository.findFirstByOrderByNewsUploadDateDesc();
        LocalDateTime now = LocalDateTime.now();

        if (news == null)
            return LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);

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
    @Transactional
    public NewsTrendDto findTrendNewsKeywords(String dateStr) {
        LocalDate date;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        //string 형태의 날짜를 local date형식으로 변환하고, 만약 변환이 안되는 경우 오늘 날짜로 설정
        try{
            date=LocalDate.parse(dateStr, dateTimeFormatter);
        }catch(Exception e)
        {
            date=LocalDate.now();
        }



        NewsTrend newsTrend = newsTrendRepository.findByTrendDate(date);
        if(newsTrend != null)
            return newsTrend.toDto();
        return NewsTrendDto.builder().build();
    }

    @Transactional
    public NewsListDto findNewsList(String keyword, Pageable pageable) {
        List<News> newsList = null;
        //전체 뉴스 반환
        if(keyword==null)
        {
            pageable = PageRequest.of(Math.max(pageable.getPageNumber() - 1, 0), 20, Sort.by("newsUploadDate").descending());
            newsList = newsRepository.findAll(pageable).toList();
        }
        //keyword에 해당하는 뉴스 반환
        else
        {
            pageable = PageRequest.of(Math.max(pageable.getPageNumber() - 1, 0), 20, Sort.by("newsNewsUploadDate").descending());
            NewsKeyword newsKeyword = newsKeywordRepository.findByKeywordContent(keyword);
            List<NewsKeywordR> newsKeywordRList= newsKeywordRRepository.findAllByNewsKeyword(newsKeyword, pageable);
            newsList = newsKeywordRList.stream().map(NewsKeywordR::getNews).collect(Collectors.toList());
        }

        List<NewsCrawledDto> newsCrawledDtoList = newsList.stream().map(News::toDto).collect(Collectors.toList());

        return NewsListDto.builder()
                .newsList(newsCrawledDtoList)
                .page(pageable.getPageNumber())
                .build();
    }

    @Transactional
    public NewsTrendDto saveNewsTrends(LocalDate localDate)
    {
        //local date에 해당하는 기간 동안의 뉴스 수집
        List<News> newsList = newsRepository.findAllByNewsUploadDateBetween(
                                LocalDateTime.of(localDate, LocalTime.of(0, 0, 0)),
                                LocalDateTime.of(localDate, LocalTime.of(23, 59, 59)));

        List<NewsCrawledDto> newsCrawledDtoList = newsList.stream().map(News::toDto).collect(Collectors.toList());


        //뉴스들을 순회하며 각 키워드들의 빈도대로 정렬
        TreeMap<Integer, NewsKeywordWithFrequencyDto> newsKeywordMap = new TreeMap<>();
        for(NewsCrawledDto newsCrawledDto : newsCrawledDtoList)
        {
            for(NewsKeywordDto newsKeywordDto : newsCrawledDto.getNewsKeywordList())
            {

                if(newsKeywordMap.containsKey(newsKeywordDto.getKeywordId()))
                {
                    NewsKeywordWithFrequencyDto newsKeywordWithFrequencyDto=newsKeywordMap.get(newsKeywordDto.getKeywordId());
                    newsKeywordWithFrequencyDto.setKeywordFrequency(newsKeywordWithFrequencyDto.getKeywordFrequency()+1);
                }
                else
                {
                    NewsKeywordWithFrequencyDto newsKeywordWithFrequencyDto = NewsKeywordWithFrequencyDto.builder()
                            .keywordId(newsKeywordDto.getKeywordId())
                            .keywordContent(newsKeywordDto.getKeywordContent())
                            .keywordFrequency(1)
                            .build();
                    newsKeywordMap.put(newsKeywordDto.getKeywordId(), newsKeywordWithFrequencyDto);
                }
            }
        }
        List<NewsKeywordWithFrequencyDto> newsKeywordList = newsKeywordMap.values().stream().sorted().collect(Collectors.toList()).subList(0, 20);


        NewsTrendDto newsTrendDto = NewsTrendDto.builder().newsKeywordList(newsKeywordList).trendDate(localDate).build();
        //먼저 이미 만들어진 오늘의 trend가 있는지 확인
        NewsTrend existTrend = newsTrendRepository.findByTrendDate(localDate);
        if(existTrend!= null)
            newsTrendRepository.delete(existTrend);
        newsTrendRepository.save(newsTrendDto.toEntity());

        return newsTrendDto;
    }
}
