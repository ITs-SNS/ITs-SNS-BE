package com.aim.itssns.scheduler;

import com.aim.itssns.domain.URLInfo;
import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.dto.NewsKeywordDto;
import com.aim.itssns.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.buf.Utf8Decoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NewsCrawler {

    private final NewsService newsService;
    private final WebClient webClient;

    //10800000L
    //from 시간 이후에 작성된 뉴스를 크롤링해오는 메소드
    @Async
    @Scheduled(fixedRate = 10800000L, initialDelay = 0L)
    public void getNewsListFromDaum() {
        //다음의 IT 뉴스 리스트를 받아올 수 있는 기본 주소
        String newsListUrl;
        //DB상에서의 가장 최신 뉴스의 업로드 시간
        LocalDateTime lastCrawlTime;
        //크롤링해서 받아온 뉴스 데이터들을 저장
        List<NewsCrawledDto> newsCrawlDtoList = new LinkedList<>();
        //crawling 하는 daum 뉴스 상의 page번호
        Long crawlPage;
        //crawling 하는 daum 뉴스 상의 날짜
        LocalDate crawlDate;
        //crawling이 끝날 조건을 만족하는지의 여부를 저장하고 있는 end flag
        boolean crawlEndFlag;
        //크롤링을 수행하는 날짜
        LocalDate today = LocalDate.now();

        //iniatialization
        lastCrawlTime = newsService.findLastNewsCrawlTime();
        crawlPage = 1L;
        crawlDate = LocalDate.now();
        crawlEndFlag = false;

        //소켓통신을 위한 자료
        Socket keywordSocket = null;
        Socket summarizeSocket = null;
        //키워드, 요약 서버로부터 읽어오기 위한 리더
        BufferedReader keywordReader = null;
        BufferedReader summarizeReader = null;
        //키워드, 요약 서버에 데이터를 보내기 위한 writer
        PrintStream keywordWriter = null;
        PrintStream summarizeWriter = null;


        try {
            keywordSocket = new Socket(URLInfo.keywordExtractionServer, URLInfo.keywordExtractionPort);
            summarizeSocket = new Socket(URLInfo.summarizeServer, URLInfo.summarizePort);
            //키워드 서버로부터 읽어오기 위한 리더
            keywordReader = new BufferedReader(new InputStreamReader(keywordSocket.getInputStream(), "utf8"));
            summarizeReader = new BufferedReader(new InputStreamReader(summarizeSocket.getInputStream(), "utf8"));
            //키워드 서버에 데이터를 보내기 위한 writer
            keywordWriter = new PrintStream(keywordSocket.getOutputStream());
            summarizeWriter = new PrintStream(summarizeSocket.getOutputStream());


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
                }

                if (crawlEndFlag)
                    break;


                //크롤링한 페이지에서 페이지 번호 정보들을 얻어옴
                String pagesStr = doc.select("#kakaoWrap")
                        .select("#kakaoContent").select("#cMain")
                        .select("#mArticle").select(".box_etc").select(".paging_news").select(".inner_paging").text().replace("현재 페이지", "");

                List<String> pageList = Arrays.asList(pagesStr.strip().split(" ").clone());

                //얻어온 페이지 번호 정보 중 가장 마지막 페이지 정보만 추출
                String lastPage = pageList.get(pageList.size() - 1);

                //다음이 없고, 크로울 페이지가 마지막 페이지 정보 +1이면 다음 날짜로 변경
                if (!lastPage.equals("다음") && Long.parseLong(lastPage) == crawlPage) {
                    crawlDate = crawlDate.minusDays(1L);
                    crawlPage = 1L;
                }

                crawlPage++;
            }

            //뉴스 요약 정보 및 키워드 정보를 읽어옴
            for (NewsCrawledDto newsCrawledDto : newsCrawlDtoList) {
                //실제 뉴스 정보(키워드, 요약 등)를 가져옴
                getNewsDetail(newsCrawledDto, keywordReader, keywordWriter, summarizeReader, summarizeWriter);
            }
            newsCrawlDtoList = newsCrawlDtoList.stream().filter(newsCrawledDto -> newsCrawledDto.getNewsKeywordList() != null).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("news목록을 가져오는데 실패하였습니다.");
            newsCrawlDtoList = new LinkedList<>();
        } finally {
            try {
                keywordReader.close();
                summarizeReader.close();
                keywordWriter.close();
                summarizeWriter.close();
                keywordSocket.close();
                summarizeSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        newsService.saveNewsCrawledDtoList(newsCrawlDtoList);

        newsService.saveNewsTrends(today);
    }


    public void getNewsDetail(NewsCrawledDto newsCrawledDto,
                               BufferedReader keywordReader,
                              PrintStream keywordWriter,
                              BufferedReader summarizeReader,
                              PrintStream summarizeWriter) throws IOException {

        Document doc = Jsoup.connect(newsCrawledDto.getNewsUrl()).get();
        Element newsElement = doc.select("#kakaoContent").first();

        //news 제목 추출
        newsCrawledDto.setNewsTitle(newsElement.select("#cSub").select(".head_view").select(".tit_view").select("h3").text());
        //기자, 업로드 시간을 받기수 위한 Elements 변수
        Elements newsInfoElements = newsElement.select("#cSub").select(".head_view").select(".info_view").select(".txt_info");
        //news 작성 기자 추출
        newsCrawledDto.setNewsReporter(newsInfoElements.first().text());
        //news 내용을 keywordExtractionDto로 추출
        String newsContent = newsElement.select("#cMain").select("#mArticle")
                .select(".news_view").select("#harmonyContainer").select("section").select("p").text();
        if(newsContent.length() > 200 && newsContent.length()<50000 && newsContent.contains(".")) {
            System.out.println("뉴스 컨텐츠: " + newsContent);
            //키워드, 요약 서버에 뉴스 내용 보냄
            keywordWriter.print(newsContent+"\n");
            summarizeWriter.print(newsContent+"\n");
            //키워드, 요약 서버로부터 키워드 추출 결과를 받아옴
            String keywordsStr = keywordReader.readLine();
            String newsSummarizeStr = summarizeReader.readLine();

            //받아온 결과를 KeywordDtoList로 바꾸고
            List<NewsKeywordDto> newsKeywordList = Arrays.asList(keywordsStr.split(" ")).stream().map(
                    keywordContent->NewsKeywordDto.builder().keywordContent(keywordContent).build()).collect(Collectors.toList());
            System.out.println(newsKeywordList);
            System.out.println("요약 정보 : " +newsSummarizeStr);
            //이를 newsCrawledDto에 내용 추가
            newsCrawledDto.setNewsKeywordList(newsKeywordList);
            newsCrawledDto.setNewsSummary(newsSummarizeStr);
        }

        //뉴스 내용을 이용하여 키워드 추출
        /*KeywordExtractionDto keywordExtractionDto = webClient.post()
                .uri(URLInfo.keywordExtractionUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newsContentDto)
                .retrieve()
                .bodyToMono(KeywordExtractionDto.class)
                .block();*/
        //System.out.println(keywordExtractionDto);
    }
}
