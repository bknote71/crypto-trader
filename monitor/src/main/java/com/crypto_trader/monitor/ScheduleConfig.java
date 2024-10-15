package com.crypto_trader.monitor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class ScheduleConfig {
    @Configuration
    public class RestTemplateConfig {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}
