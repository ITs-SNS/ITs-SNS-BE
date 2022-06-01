package com.aim.itssns.scheduler;

import com.aim.itssns.domain.URLInfo;
import com.aim.itssns.domain.dto.RecruitCrawledDto;
import com.aim.itssns.domain.dto.RecruitKeywordDto;
import com.aim.itssns.service.RecruitService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecruitCrawler {
    private final RecruitService recruitService;
    private WebDriver driver;
    @Async
    @Scheduled(fixedRate = 180L*60000L, initialDelay = 0L)
    public void getRecruitListFromSaramin()
    {
        String recruitListUrl;
        String recruitListUrlQuery;
        String lastCrawlUrl;
        LinkedList<RecruitCrawledDto> recruitCrawlDtoList = new LinkedList<>();
        Long crawlPage;
        LocalDate crawlDate;
        boolean crawlEndFlag;

        //iniatialization
        lastCrawlUrl = recruitService.findLastRecruitUrl();

        crawlPage = 1L;
        crawlDate = LocalDate.now();
        crawlEndFlag=false;
        try {
            System.setProperty("webdriver.chrome.driver", URLInfo.chromeDriverPath);
            //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)
            driver = new ChromeDriver();

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
                            .recruitCrawlDate(crawlDate)
                            .build();
                    if(recruitUrl.equals(lastCrawlUrl)) {
                        crawlEndFlag = true;
                        break;
                    }
//                    recruitCrawlDtoList.addFirst(recruit);
                    //System.out.println(recruitUrl);
                    recruitCrawlDtoList.addFirst(recruit);
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
                if(lastCrawlUrl.equals("") && crawlPage==7)
                    break;
            }
            for(RecruitCrawledDto recruitCrawledDto :recruitCrawlDtoList)
                getRecruitFromSaramin(recruitCrawledDto);
            //recruit_end_date이 null인 것들은 날려줌
            recruitCrawlDtoList = recruitCrawlDtoList.stream().filter(recruitCrawledDto ->
                    recruitCrawledDto.getRecruitEndDate() != null && recruitCrawledDto.getRecruitStartDate() != null).collect(Collectors.toCollection(LinkedList::new));


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("recruit 목록을 가져오는데 실패하였습니다.");
            recruitCrawlDtoList = new LinkedList<>();
        }
        System.out.println("Scheduled recruit Crawler End");
        System.out.println(recruitCrawlDtoList.size());
        recruitService.saveRecruitCrawledDtoList(recruitCrawlDtoList);
        recruitService.saveRecruitTrend(crawlDate);
        driver.close();	//탭 닫기
        driver.quit();	//브라우저 닫기
    }
    public RecruitCrawledDto getRecruitFromSaramin(RecruitCrawledDto recruitCrawledDto) throws IOException, InterruptedException {

        String recruitUrl = recruitCrawledDto.getRecruitUrl();
        System.out.println("recruitUrl = " + recruitUrl);
        try {
            driver.get(recruitUrl);
        }catch(WebDriverException e)
        {
            return recruitCrawledDto;
        }
        Thread.sleep(1000);
        String recruitStartDate=null;
        String recruitEndDate=null;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime = null;
        String companyName=null;
        List<RecruitKeywordDto> keywordList = new ArrayList<>();
        String recruitTitle = null;
        Boolean noElementFlag = false;
        try {
            companyName = driver.findElement(By.className("company")).getText();
            recruitCrawledDto.setRecruitCompany(companyName);
            System.out.println(recruitCrawledDto.getRecruitCompany());
            recruitStartDate = driver.findElement(By.className("info_period")).findElements(By.tagName("dd")).get(0).getText();
        }catch(Exception e) {
            recruitStartDate = null;
            noElementFlag = true;
            System.out.println("해당공고는 [공고 사전 확인 서비스] 진행 중입니다.");
            recruitCrawledDto.setRecruitCompany(null);
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
            startDateTime = LocalDateTime.parse(recruitStartDate, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            if (recruitEndDate != "")
                endDateTime = LocalDateTime.parse(recruitEndDate, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            recruitCrawledDto.setRecruitStartDate(startDateTime);
            recruitCrawledDto.setRecruitEndDate(endDateTime);
            recruitTitle = driver.findElement(By.className("tit_job")).getText();
            recruitCrawledDto.setRecruitTitle(recruitTitle);
            System.out.println("recruitTitle = " + recruitTitle);
            List<WebElement> webElementList = driver.findElement(By.className("scroll")).findElements(By.tagName("li"));


            Integer i = 0;
            for (WebElement webElement : webElementList){
                String keywordName = webElement.getText();//.substring(1);
                if(keywordName.equals("")) break;
                keywordName = keywordName.substring(1);
                System.out.println("keywordName = " + keywordName);
                RecruitKeywordDto recruitKeywordDto = new RecruitKeywordDto();
                recruitKeywordDto.setKeywordContent(keywordName);
                keywordList.add(i, recruitKeywordDto);
                i++;
            }

        }
        recruitCrawledDto.setRecruitKeywordList(keywordList);
        return recruitCrawledDto;
    }
}