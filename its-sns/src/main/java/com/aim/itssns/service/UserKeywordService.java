package com.aim.itssns.service;


import com.aim.itssns.domain.URLInfo;
import com.aim.itssns.domain.dto.UserKeywordDto;
import com.aim.itssns.domain.entity.NewsKeywordR;
import com.aim.itssns.domain.entity.RecruitKeywordR;
import com.aim.itssns.domain.entity.UserKeyword;
import com.aim.itssns.repository.NewsKeywordRRepository;
import com.aim.itssns.repository.RecruitKeywordRRepository;
import com.aim.itssns.repository.UserKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserKeywordService {
    private final JavaMailSender mailSender;
    private final UserKeywordRepository userKeywordRepository;
    private final NewsKeywordRRepository newsKeywordRRepository;
    private final RecruitKeywordRRepository recruitKeywordRRepository;


    @Transactional
    public UserKeywordDto save(UserKeywordDto userKeywordDto) {
        return userKeywordRepository.save(userKeywordDto.toEntity()).toDto();
    }


    @Transactional
    public void sendUsersAboutKeywords() {

        HashMap<String, ArrayList<String>> userMap = new HashMap<>();

        //등록된 정보를 가지고 메일을 보내기위해 모든 키워드 구독정보를 읽어옴
        List<UserKeyword> userKeywordList = userKeywordRepository.findAll();
        for (UserKeyword userKeyword : userKeywordList) {
            //news type의 키워드를 구독한 경우
            if (userKeyword.getUserKeywordType().equals("news")) {
                //해당 키워드에 해당하는 뉴스들을 모두 찾음
                List<NewsKeywordR> newsKeywordRList = newsKeywordRRepository.findAllByNewsKeywordKeywordContent(userKeyword.getUserKeywordContent());
                for (NewsKeywordR newsKeywordR : newsKeywordRList) {
                    //뉴스 정보를 hashmap에 등록
                    if (!userMap.containsKey(userKeyword.getUserEmail())) {
                        userMap.put(userKeyword.getUserEmail(), new ArrayList<>());
                    }
                    userMap.get(userKeyword.getUserEmail()).add(newsKeywordR.getNews().toDto().mailContent());
                }
            }
            //recruit type의 키워드를 구독한 경우
            else if (userKeyword.getUserKeywordType().equals("recruit")) {
                List<RecruitKeywordR> recruitKeywordRList = recruitKeywordRRepository.findAllByRecruitKeywordKeywordContent(userKeyword.getUserKeywordContent());
                for (RecruitKeywordR recruitKeywordR : recruitKeywordRList) {
                    //뉴스 정보를 hashmap에 등록
                    if (!userMap.containsKey(userKeyword.getUserEmail())) {
                        userMap.put(userKeyword.getUserEmail(), new ArrayList<>());
                    }
                    userMap.get(userKeyword.getUserEmail()).add(recruitKeywordR.getRecruit().toDto().mailContent());
                }
            }
        }
        //각 user Email을 돌면서 mail을 해당 유저에게 보내줌
        for (Map.Entry<String, ArrayList<String>> entry : userMap.entrySet()) {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            try {
                mimeMessageHelper.setFrom(URLInfo.fromMail);
                mimeMessageHelper.setSubject(URLInfo.mailSubject);
                mimeMessageHelper.setTo(entry.getKey());
                StringBuilder mailContents = new StringBuilder();
                for (String content : entry.getValue()) {
                    mailContents.append(content);
                }
                mimeMessage.setContent(mailContents.toString(), "text/plain; charset=utf-8");
                mailSender.send(mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

    }


}
