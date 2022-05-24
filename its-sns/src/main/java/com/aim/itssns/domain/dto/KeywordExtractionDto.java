package com.aim.itssns.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Data
public class KeywordExtractionDto {
    private String content;
    private String keywords;
}
