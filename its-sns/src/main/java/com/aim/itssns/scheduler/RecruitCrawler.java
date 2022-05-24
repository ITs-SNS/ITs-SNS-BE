package com.aim.itssns.scheduler;

import com.aim.itssns.domain.URLInfo;
import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.domain.dto.RecruitCrawledDto;
import com.aim.itssns.service.NewsService;
import com.aim.itssns.service.RecruitService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
    private final RecruitService recruitService;
    private WebDriver driver;
   // @Async
   // @Scheduled(fixedRate = 180000L, initialDelay = 0L)
    public void getRecruitListFromSaramin()
    {
        System.setProperty("webdriver.chrome.driver", "/Users/dugunhee/ITs-SNS-BE/chromedriver");
        //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)
        driver = new ChromeDriver();
        String recruitListUrl;
        String recruitListUrlQuery;
        //마지막에 크롤링한 recruit url
        String lastCrawlUrl;
        //크롤링해서 받아온 뉴스 데이터들을 저장
        LinkedList<RecruitCrawledDto> recruitCrawlDtoList = new LinkedList<>();
        //crawling 하는 daum 뉴스 상의 page번호
        Long crawlPage;
        //crawling 하는 daum 뉴스 상의 날짜
        LocalDate crawlDate;
        //crawling이 끝날 조건을 만족하는지의 여부를 저장하고 있는 end flag
        boolean crawlEndFlag;


        //iniatialization
        lastCrawlUrl = recruitService.findLastRecruitUrl();

        crawlPage = 1L;
        crawlDate = LocalDate.now();
        crawlEndFlag=false;
        try {
            while(true) {
                //crawlPage에 해당하는 뉴스들의 주소, 업로드 시간을 추출
                String crawlDateStr = crawlDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Document doc = Jsoup.connect(URLInfo.recruitListUrl + "?page=" + crawlPage + URLInfo.recruitListUrlQuery).timeout(0).maxBodySize(1024*1024*3).get();
                Elements recruitElements = doc
                        .select("#sri_section")
                        .select("#sri_wrap").select("#content")
                        .select(".common_recruilt_list")
                        .select("#default_list_wrap")
                        .select(".list_recruiting").select(".list_body")
                        .select(".list_item");

                for (Element recruitElement : recruitElements) {
                    //뉴스 각각의 주소를 추출
                    String recruitUrl = URLInfo.saraminUrl+recruitElement.select(".job_tit")
                            .select(".str_tit").attr("href");

                    RecruitCrawledDto recruit = RecruitCrawledDto.builder()
                            .recruitUrl(recruitUrl)
                            .build();

                    if(recruitUrl.equals(lastCrawlUrl)) {
                        crawlEndFlag = true;
                        break;
                    }
                    recruitCrawlDtoList.addFirst(recruit);
                    //System.out.println(recruitUrl);
                    getRecruitFromSaramin(recruit);
                }
                if(crawlEndFlag)
                    break;
                //크롤링한 페이지에서 페이지 번호 정보들을 얻어옴
                String pagesStr = doc.select("#sri_section")
                        .select("#sri_wrap").select("#content")
                        .select(".common_recruilt_list")
                        .select("#default_list_wrap")
                        .select(".pagination")//.select(".page")
                        .text();

                List<String> pageList = Arrays.asList(pagesStr.split(" "));
                //System.out.println(pagesStr);
                //얻어온 페이지 번호 정보 중 가장 마지막 페이지 정보만 추출
                String lastPage = pageList.get(pageList.size()-1);

                //다음이 없고, 크로울 페이지가 마지막 페이지 정보 +1이면 다음 날짜로 변경
                if(!lastPage.equals("다음") && Long.parseLong(lastPage) == crawlPage)
                {
                    break;
                }
                System.out.println(crawlPage);
                crawlPage++;
                if(crawlPage == 30) break;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("recruit 목록을 가져오는데 실패하였습니다.");
            recruitCrawlDtoList = new LinkedList<>();
        }
        System.out.println(recruitCrawlDtoList.size());
        recruitService.saveRecruitCrawledList(recruitCrawlDtoList);
        driver.close();	//탭 닫기
        driver.quit();	//브라우저 닫기
    }
    public void getRecruitFromSaramin(RecruitCrawledDto recruitCrawledDto) throws IOException, InterruptedException {
        String recruitUrl = recruitCrawledDto.getRecruitUrl();
        System.out.println("recruitUrl = " + recruitUrl);
        driver.get(recruitUrl);
        Thread.sleep(1000);

        recruitCrawledDto.setRecruitCompany(driver.findElement(By.className("company")).getText());
        System.out.println(recruitCrawledDto.getRecruitCompany());
        String recruitStartDate=null;
        String recruitEndDate=null;
        Boolean noElementFlag = false;
        try {
            recruitStartDate = driver.findElement(By.className("info_period")).findElements(By.tagName("dd")).get(0).getText();
        }catch(Exception e) {
            recruitStartDate = null;
            noElementFlag = true;
            System.out.println("해당공고는 [공고 사전 확인 서비스] 진행 중입니다.");
            recruitCrawledDto.setRecruitStartDate(null);
            recruitCrawledDto.setRecruitEndDate(null);
            recruitCrawledDto.setRecruitTitle("해당공고는 [공고 사전 확인 서비스] 진행 중입니다.");
        }
        if(noElementFlag == false) {
            if (driver.findElement(By.className("info_period")).findElements(By.tagName("dd")).size() == 1)
                recruitEndDate = "";
            else
                recruitEndDate = driver.findElement(By.className("info_period")).findElements(By.tagName("dd")).get(1).getText();
            System.out.println("recruitStartDate = " + recruitStartDate);
            System.out.println("recruitEndDate = " + recruitEndDate);
            if (recruitStartDate != "") recruitStartDate += ":00";
            if (recruitEndDate != "") recruitEndDate += ":00";
            LocalDateTime startDateTime = LocalDateTime.parse(recruitStartDate, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            LocalDateTime endDateTime = null;
            if (recruitEndDate != "")
                endDateTime = LocalDateTime.parse(recruitEndDate, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            recruitCrawledDto.setRecruitStartDate(startDateTime);
            recruitCrawledDto.setRecruitEndDate(endDateTime);
            String recruitTitle = driver.findElement(By.className("tit_job")).getText();
            recruitCrawledDto.setRecruitTitle(recruitTitle);
            System.out.println("recruitTitle = " + recruitTitle);
        }
    }
}