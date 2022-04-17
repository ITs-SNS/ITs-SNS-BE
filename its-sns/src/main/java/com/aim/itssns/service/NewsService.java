package com.aim.itssns.service;

import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.entity.News;
import com.aim.itssns.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    //news Repository에서 시간 가져옴, 만약 디비에 뉴스정보가 존재하지 않으면 2022/4/16일을 lastCrawlTime으로 함.
    public LocalDateTime findLastNewsCrawlTime()
    {
        News news = newsRepository.findFirstByOrderByNewsUploadDateDesc();
        if(news == null)
            return LocalDateTime.of(2022, 4, 16, 0,0,0);

        return news.getNewsUploadDate();
    }

    public void saveNewsCrawledList(List<NewsCrawledDto> newsCrawledDtoList)
    {
        List<News> newsList =  newsCrawledDtoList.stream().map(newsCrawledDto -> newsCrawledDto.toEntity()).collect(Collectors.toList());
        newsRepository.saveAll(newsList);
    }

}
