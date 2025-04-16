package cs.tarun.QuizApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient openTdbWebClient() {
        return WebClient.builder()
                .baseUrl("https://opentdb.com")
                .build();
    }
}
