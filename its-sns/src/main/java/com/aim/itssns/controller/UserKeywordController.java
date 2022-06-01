package com.aim.itssns.controller;


import com.aim.itssns.domain.dto.UserKeywordDto;
import com.aim.itssns.service.UserKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserKeywordController {

    private final UserKeywordService userKeywordService;

    @PostMapping("/user_keyword")
    @ResponseStatus(HttpStatus.CREATED)
    public UserKeywordDto postUserKeyword(@RequestBody UserKeywordDto userKeywordDto)
    {
        System.out.println(userKeywordDto);
        return userKeywordService.save(userKeywordDto);
    }

    @GetMapping("/send_mail")
    @ResponseStatus(HttpStatus.OK)
    public void sendMail()
    {
        userKeywordService.sendUsersAboutKeywords();
    }
}
