package com.aim.itssns.scheduler;

import com.aim.itssns.domain.URLInfo;
import com.aim.itssns.domain.dto.NewsCrawledDto;
import com.aim.itssns.service.UserKeywordService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MailSender {
    private final UserKeywordService userKeywordService;

    @Async
    @Scheduled(cron = "0 0 10 * * *")
    public void sendMailsToUsers() {
        userKeywordService.sendUsersAboutKeywords(LocalDate.now().minusDays(1));
    }

}
