package com.aim.itssns.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.apache.http.client.HttpClient;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  //5초
        factory.setReadTimeout(3000);     //5초

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionReuseStrategy(((response, context) -> false))
                .setMaxConnTotal(50)
                .setMaxConnPerRoute(20)
                .build();
        factory.setHttpClient(httpClient);
        return new RestTemplate(factory);
    }
}

