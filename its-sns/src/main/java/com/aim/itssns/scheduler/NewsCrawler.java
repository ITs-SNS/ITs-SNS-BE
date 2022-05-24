package com.aim.itssns.scheduler;

import com.aim.itssns.domain.URLInfo;
import com.aim.itssns.domain.dto.KeywordExtractionDto;
import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsCrawler {

    private final NewsService newsService;
    private final WebClient webClient;

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
        lastCrawlTime = newsService.findLastNewsCrawlTime();
        crawlPage = 1L;
        crawlDate = LocalDate.now();
        crawlEndFlag = false;


        try {
            while (true) {
                //crawlPage에 해당하는 뉴스들의 주소, 업로드 시간을 추출
                String crawlDateStr = crawlDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Document doc = Jsoup.connect(URLInfo.newsListUrl + "?page=" + crawlPage + "&regDate=" + crawlDateStr).get();
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

                    //뉴스 url과 업로드날짜 정보로만 일단은 news 구성
                    NewsCrawledDto newsCrawledDto = NewsCrawledDto.builder()
                            .newsUrl(newsUrl)
                            .newsUploadDate(newsUploadDate)
                            .build();

                    //만약 해당 뉴스가 lastCrawlTime 같거나 이전의 뉴스이면 더이상 크롤링 할 필요가 없음
                    if (newsUploadDate.isBefore(lastCrawlTime) || newsUploadDate.isEqual(lastCrawlTime)) {
                        crawlEndFlag = true;
                        break;
                    }

                    newsCrawlDtoList.add(newsCrawledDto);

                    //TODO: 나중에 thread로 처리하기
                    //실제 뉴스 정보(키워드, 요약 등)를 가져옴
                    getNewsDetail(newsCrawledDto);
                }

                if (crawlEndFlag)
                    break;


                //크롤링한 페이지에서 페이지 번호 정보들을 얻어옴
                String pagesStr = doc.select("#kakaoWrap")
                        .select("#kakaoContent").select("#cMain")
                        .select("#mArticle").select(".box_etc").select(".paging_news").select(".inner_paging").text().replace("현재 페이지", "");

                List<String> pageList = Arrays.asList(pagesStr.split(" ").clone());

                //얻어온 페이지 번호 정보 중 가장 마지막 페이지 정보만 추출
                String lastPage = pageList.get(pageList.size() - 1);

                //다음이 없고, 크로울 페이지가 마지막 페이지 정보 +1이면 다음 날짜로 변경
                if (!lastPage.equals("다음") && Long.parseLong(lastPage) == crawlPage) {
                    crawlDate = crawlDate.minusDays(1L);
                    crawlPage = 1L;
                }

                crawlPage++;
            }
        } catch (Exception e) {
            System.out.println("news목록을 가져오는데 실패하였습니다.");
            newsCrawlDtoList = new LinkedList<>();
        }
        System.out.println("끝");
        //newsService.saveNewsCrawledDtoList(newsCrawlDtoList);

    }


    public void getNewsDetail(NewsCrawledDto newsCrawledDto) throws IOException {

        Document doc = Jsoup.connect(newsCrawledDto.getNewsUrl()).get();
        Element newsElement = doc.select("#kakaoContent").first();

        //news 제목 추출
        newsCrawledDto.setNewsTitle(newsElement.select("#cSub").select(".head_view").select(".tit_view").select("h3").text());
        //기자, 업로드 시간을 받기수 위한 Elements 변수
        Elements newsInfoElements = newsElement.select("#cSub").select(".head_view").select(".info_view").select(".txt_info");
        //news 작성 기자 추출
        newsCrawledDto.setNewsReporter(newsInfoElements.first().text());
        //news 내용을 keywordExtractionDto로 추출
        KeywordExtractionDto newscontentDto = KeywordExtractionDto.builder().content(newsElement.select("#cMain").select("#mArticle")
                .select(".news_view").select("#harmonyContainer").select("section").select("p").text()).build();

        System.out.println(newscontentDto);
        //뉴스 내용을 이용하여 키워드 추출
        KeywordExtractionDto keywordExtractionDto = webClient.post()
                .uri("/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newscontentDto)
                .retrieve()
                .bodyToMono(KeywordExtractionDto.class)
                .block();
        System.out.println(keywordExtractionDto);
    }
}
