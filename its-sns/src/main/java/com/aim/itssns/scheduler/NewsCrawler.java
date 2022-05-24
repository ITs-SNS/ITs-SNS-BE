package com.aim.itssns.scheduler;

import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.dto.NewsKeywordDto;
import com.aim.itssns.domain.entity.NewsKeyword;
import com.aim.itssns.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsCrawler {

    private final NewsService newsService;

    //10800000L
    //from 시간 이후에 작성된 뉴스를 크롤링해오는 메소드
    @Async
    @Scheduled(fixedRate = 180000L, initialDelay = 0L)
    public void getNewsListFromDaum() {

        //다음의 IT 뉴스 리스트를 받아올 수 있는 기본 주소
        String newsListUrl;
        //DB상에서의 가장 최신 뉴스의 업로드 시간
        LocalDateTime lastCrawlTime;
        //크롤링해서 받아온 뉴스 데이터들을 저장
        LinkedList<NewsCrawledDto> newsCrawlDtoList = new LinkedList<>();
        //crawling 하는 daum 뉴스 상의 page번호
        Long crawlPage;
        //crawling 하는 daum 뉴스 상의 날짜
        LocalDate crawlDate;
        //crawling이 끝날 조건을 만족하는지의 여부를 저장하고 있는 end flag
        boolean crawlEndFlag;


        //iniatialization
        newsListUrl = "https://news.daum.net/breakingnews/digital";
        lastCrawlTime = newsService.findLastNewsCrawlTime();
        crawlPage = 1L;
        crawlDate = LocalDate.now();
        crawlEndFlag=false;



        try {
            while(true) {
                //crawlPage에 해당하는 뉴스들의 주소, 업로드 시간을 추출
                String crawlDateStr = crawlDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Document doc = Jsoup.connect(newsListUrl + "?page=" + crawlPage + "&regDate=" + crawlDateStr).get();
                Elements newsElements = doc.select("#kakaoWrap")
                        .select("#kakaoContent").select("#cMain")
                        .select("#mArticle").select(".box_etc")
                        .select(".list_news2").select(".cont_thumb").select(".tit_thumb");

                for (Element newsElement : newsElements) {

                    //뉴스 각각의 주소를 추출
                    String newsUrl = newsElement.select("a").attr("href");

                    //뉴스 각각의 업로드 시간 추출
                    String newsUploadDateStr = crawlDate + " " + newsElement.select(".info_news").select(".info_time").text();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime newsUploadDate = LocalDateTime.parse(newsUploadDateStr, formatter);


                    //TODO: 키워드 추출 받아오기
                    ArrayList<NewsKeywordDto> newsKeywordDtos = new ArrayList<>();
                    newsKeywordDtos.add(NewsKeywordDto.builder().keywordContent("인공지능").build());
                    newsKeywordDtos.add(NewsKeywordDto.builder().keywordContent("구글").build());
                    newsKeywordDtos.add(NewsKeywordDto.builder().keywordContent("URL:"+newsUrl).build());

                    NewsCrawledDto newsCrawledDto = NewsCrawledDto.builder()
                            .newsUrl(newsUrl)
                            .newsKeywordList(newsKeywordDtos)
                            .newsUploadDate(newsUploadDate)
                            .build();

                    //만약 해당 뉴스가 lastCrawlTime 같거나 이전의 뉴스이면 더이상 크롤링 할 필요가 없음
                    if(newsUploadDate.isBefore(lastCrawlTime) || newsUploadDate.isEqual(lastCrawlTime)) {
                        crawlEndFlag = true;
                        break;
                    }
                    newsCrawlDtoList.add(newsCrawledDto);
                    //getNewsFromDaum(news);
                }

                if(crawlEndFlag)
                    break;


                //크롤링한 페이지에서 페이지 번호 정보들을 얻어옴
                String pagesStr = doc.select("#kakaoWrap")
                        .select("#kakaoContent").select("#cMain")
                        .select("#mArticle").select(".box_etc").select(".paging_news").select(".inner_paging").text().replace("현재 페이지", "");

                List<String> pageList = Arrays.asList(pagesStr.split(" ").clone());

                //얻어온 페이지 번호 정보 중 가장 마지막 페이지 정보만 추출
                String lastPage = pageList.get(pageList.size()-1);

                //다음이 없고, 크로울 페이지가 마지막 페이지 정보 +1이면 다음 날짜로 변경
                if(!lastPage.equals("다음") && Long.parseLong(lastPage) == crawlPage)
                {
                    crawlDate=crawlDate.minusDays(1L);
                    crawlPage=1L;
                }

                crawlPage++;
            }
        } catch (IOException e) {
            System.out.println("news목록을 가져오는데 실패하였습니다.");
            newsCrawlDtoList = new LinkedList<>();
        }

        newsService.saveNewsCrawledDtoList(newsCrawlDtoList);

    }



    public void getNewsFromDaum(NewsCrawledDto newsCrawledDto) throws IOException {

        Document doc = Jsoup.connect(newsCrawledDto.getNewsUrl()).get();
        Element newsElement = doc.select("#kakaoContent").first();


        //news 제목 추출
        newsCrawledDto.setNewsTitle(newsElement.select("#cSub").select(".head_view").select(".tit_view").select("h3").text());

        //기자, 업로드 시간을 받기수 위한 Elements 변
        Elements newsInfoElements = newsElement.select("#cSub").select(".head_view").select(".info_view").select(".txt_info");

        //news 작성 기자 추출
        newsCrawledDto.setNewsReporter(newsInfoElements.first().text());

        //news 내용 추출
       // newsCrawledDto.setNewsContent(newsElement.select("#cMain").select("#mArticle").select(".news_view")
         //       .select("#harmonyContainer").select("section").select("p").text());


    }
}
