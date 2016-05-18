package com.example;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
public class EdgeServiceApplication {
//    @Bean
//    @LoadBalanced
//    RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    public static void main(String[] args) {
        SpringApplication.run(EdgeServiceApplication.class, args);
    }
}

@RestController
@RefreshScope
class QuoteController {
    @Autowired
    @LoadBalanced
    OAuth2RestOperations restTemplate;

    @Value("${quote}")
    private String defaultQuote;

    @LoadBalanced
    @RequestMapping("/quotorama")
//    @HystrixCommand(fallbackMethod = "getDefaultQuote")
    public String getRandomQuote() {
        return restTemplate.getForObject("http://quote-service/random", String.class);
    }

    public String getDefaultQuote() {
        return defaultQuote;
    }
}

class Quote {
    private Long id;
    private String text;
    private String source;

    public Quote(Long id) {
        this.id = id;
    }

    public Quote(String text, String source) {
        this.text = text;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}