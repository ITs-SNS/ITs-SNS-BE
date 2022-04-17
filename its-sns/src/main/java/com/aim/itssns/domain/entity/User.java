package com.aim.itssns.domain.entity;


import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Id
    @Column(name = "user_id",nullable = false)
    private Integer userId;
    @Column(nullable = false)
    private String userEmail;
    @Column(nullable = false)
    private String userPassword;
    @Column(nullable = false)
    private String userName;

    @OneToMany(mappedBy = "user")
    private List<UserKeyword> userKeywordList = new ArrayList<>();
}
