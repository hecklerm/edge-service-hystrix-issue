package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@SpringBootApplication
@EnableZuulProxy
@EnableOAuth2Sso
public class EdgeServiceApplication {
    @Bean
    @LoadBalanced
    OAuth2RestTemplate restTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
        return new OAuth2RestTemplate(resource, context);
    }

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

    @Value("${quote:default}")
    private String defaultQuote;

    @LoadBalanced
    @RequestMapping("/quotorama")
	@HystrixCommand(fallbackMethod = "getDefaultQuote")
    public String getRandomQuote() {
        return this.restTemplate.getForObject("http://quote-service/random", String.class);
    }

    public String getDefaultQuote() {
        return this.defaultQuote;
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
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public String getSource() {
        return this.source;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + this.id +
                ", text='" + this.text + '\'' +
                ", source='" + this.source + '\'' +
                '}';
    }
}