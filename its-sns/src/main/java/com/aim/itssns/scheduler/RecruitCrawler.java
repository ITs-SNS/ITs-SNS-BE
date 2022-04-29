package com.aim.itssns.scheduler;

import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.dto.RecruitCrawledDto;
import com.aim.itssns.service.NewsService;
import com.aim.itssns.service.RecruitService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecruitCrawler {
//    https://www.saramin.co.kr/zf_user/jobs/list/job-category
//    ?page=3
//     &cat_mcls=2&search_optional_item=n&search_done=y&panel_count=y
//     &sort=RD
//     &is_param=1&isSearchResultEmpty=1
//     &isSectionHome=0
//     &searchParamCount=1
//     &tab_type=all
//     &cat_nm%5B2%5D=IT%EA%B0%9C%EB%B0%9C%C2%B7%EB%8D%B0%EC%9D%B4%ED%84%B0+%EC%A0%84%EC%B2%B4
//     &recruit_kind=recruit
//     &quick_apply=n&isAjaxRequest=0
//     &page_count=50
//     &type=job-category#searchTitle
    private final RecruitService recruitService;

    @Async
    @Scheduled(fixedRate = 60000L, initialDelay = 0L)
    public void getRecruitListFromSaramin()
    {
        String recruitListUrl;
        String recruitListUrlQuery;
        //DB상에서의 가장 최신 뉴스의 업로드 시간
        LocalDateTime lastCrawlTime;
        //크롤링해서 받아온 뉴스 데이터들을 저장
        LinkedList<RecruitCrawledDto> recruitCrawlDtoList = new LinkedList<>();
        //crawling 하는 daum 뉴스 상의 page번호
        Long crawlPage;
        //crawling 하는 daum 뉴스 상의 날짜
        LocalDate crawlDate;
        //crawling이 끝날 조건을 만족하는지의 여부를 저장하고 있는 end flag
        boolean crawlEndFlag;


        //iniatialization
        recruitListUrl = "https://www.saramin.co.kr/zf_user/jobs/list/job-category";
        recruitListUrlQuery = "&cat_mcls=2&search_optional_item=n&search_done=y&panel_count=y&sort=RD&is_param=1&isSearchResultEmpty=1&isSectionHome=0&searchParamCount=1&tab_type=all&cat_nm%5B2%5D=IT%EA%B0%9C%EB%B0%9C%C2%B7%EB%8D%B0%EC%9D%B4%ED%84%B0+%EC%A0%84%EC%B2%B4&recruit_kind=recruit&quick_apply=n&isAjaxRequest=0&page_count=50&type=job-category#searchTitle";
        lastCrawlTime = recruitService.findLastRecruitCrawlTime();

        crawlPage = 2L;
        crawlDate = LocalDate.now();
        crawlEndFlag=false;
        try {
            while(true) {
                //crawlPage에 해당하는 뉴스들의 주소, 업로드 시간을 추출
                String crawlDateStr = crawlDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Document doc = Jsoup.connect(recruitListUrl + "?page=" + crawlPage + recruitListUrlQuery).get();
                System.out.println(recruitListUrl + "?page=" + crawlPage + recruitListUrlQuery);
                Elements recruitElements = doc
                        .select("#sri_section")
                        .select("#sri_wrap").select("#content")
                        .select(".common_recruilt_list")
                        .select("#default_list_wrap")
                        .select(".list_recruiting").select(".list_body")
                        .select(".list_item");
                System.out.println(recruitElements);
                for (Element recruitElement : recruitElements) {
                    System.out.println(recruitElement);
                    //뉴스 각각의 주소를 추출
                    String recruitUrl = recruitElement.select(".col.notification_info").select(".job_tit")
                            .select(".str_tit").select("a").attr("href");
                    System.out.println(recruitUrl);
                    /*
                    //뉴스 각각의 업로드 시간 추출
                    String recruitUploadDateStr = crawlDate + " " + recruitElement.select(".info_news").select(".info_time").text();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime newsUploadDate = LocalDateTime.parse(newsUploadDateStr, formatter);

                    NewsCrawledDto news = NewsCrawledDto.builder()
                            .newsUrl(newsUrl)
                            .newsUploadDate(newsUploadDate)
                            .build();

                    //만약 해당 뉴스가 lastCrawlTime 같거나 이전의 뉴스이면 더이상 크롤링 할 필요가 없
                    if(newsUploadDate.isBefore(lastCrawlTime) || newsUploadDate.isEqual(lastCrawlTime)) {

                        crawlEndFlag = true;
                        break;
                    }



                    newsCrawlDtoList.add(news);
                    System.out.println(news);
                    //getNewsFromDaum(news);
                    */
                }
                crawlEndFlag = true;
                if(crawlEndFlag)
                    break;

/*
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

 */
            }
        } catch (IOException e) {
            System.out.println("recruit 목록을 가져오는데 실패하였습니다.");
            recruitCrawlDtoList = new LinkedList<>();
        }

        recruitService.saveRecruitCrawledList(recruitCrawlDtoList);
    }
    public void getRecruitFromSaramin(RecruitCrawledDto recruitCrawledDto) throws IOException {

        Document doc = Jsoup.connect(recruitCrawledDto.getRecruitUrl()).get();
        Element recruitElement = doc.select("").first();


        //
        recruitCrawledDto.setRecruitTitle(recruitElement.select("#cSub").select(".head_view").select(".tit_view").select("h3").text());

        //기자, 업로드 시간을 받기수 위한 Elements 변
        Elements newsInfoElements = recruitElement.select("#cSub").select(".head_view").select(".info_view").select(".txt_info");

        //채용 회사 추출
        recruitCrawledDto.setRecruitCompany(newsInfoElements.first().text());


    }
}
