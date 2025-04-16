package cs.tarun.QuizApp.config;

import cs.tarun.QuizApp.services.OpenTDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CategoryInitializer {
    private final OpenTDBService openTDBService;

    @Bean
    public CommandLineRunner initializeCategories() {
        return args -> {
            // Fetch and sync categories from OpenTDB API when application starts
            openTDBService.syncCategories();
            System.out.println("Categories synchronized from OpenTDB API");
        };
    }
}
