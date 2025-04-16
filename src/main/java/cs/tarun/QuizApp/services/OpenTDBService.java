package cs.tarun.QuizApp.services;

import cs.tarun.QuizApp.models.Category;
import cs.tarun.QuizApp.models.QuizTournament;
import cs.tarun.QuizApp.models.TournamentQuestion;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenTDBService {
    private final WebClient webClient;
    private final CategoryService categoryService;

    public List<TournamentQuestion> fetchQuestions(QuizTournament.Difficulty difficulty) {
        // Map difficulty to OpenTDB difficulty
        String openTDBDifficulty = mapDifficultyToOpenTDB(difficulty);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api.php")
                        .queryParam("amount", 10)
                        .queryParam("difficulty", openTDBDifficulty)
                        .queryParam("type", "multiple") // You can modify this if you want to include true/false
                        .build())
                .retrieve()
                .bodyToMono(OpenTDBResponse.class)
                .block() // Be cautious with blocking in production
                .getResults()
                .stream()
                .map(this::convertToTournamentQuestion)
                .collect(Collectors.toList());
    }

    public List<TournamentQuestion> fetchQuestionsByCategory(QuizTournament.Difficulty difficulty, Long categoryId) {
        // Map difficulty to OpenTDB difficulty
        String openTDBDifficulty = mapDifficultyToOpenTDB(difficulty);

        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (category.getOpenTdbId() == null) {
            throw new RuntimeException("Category does not have a valid OpenTDB ID");
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api.php")
                        .queryParam("amount", 10)
                        .queryParam("difficulty", openTDBDifficulty)
                        .queryParam("category", category.getOpenTdbId())
                        .queryParam("type", "multiple")
                        .build())
                .retrieve()
                .bodyToMono(OpenTDBResponse.class)
                .block() // Be cautious with blocking in production
                .getResults()
                .stream()
                .map(apiQuestion -> convertToTournamentQuestion(apiQuestion, category))
                .collect(Collectors.toList());
    }

    // Fetch categories from OpenTDB
    public List<Category> fetchCategories() {
        return webClient.get()
                .uri("/api_category.php")
                .retrieve()
                .bodyToMono(OpenTDBCategoryResponse.class)
                .block()
                .getTriviaCategories()
                .stream()
                .map(this::convertToCategory)
                .collect(Collectors.toList());
    }

    // Sync categories from OpenTDB to local database
    public void syncCategories() {
        List<Category> openTdbCategories = fetchCategories();
        for (Category category : openTdbCategories) {
            categoryService.findOrCreateCategory(category.getName(), category.getOpenTdbId());
        }
    }

    private String mapDifficultyToOpenTDB(QuizTournament.Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return "easy";
            case MEDIUM: return "medium";
            case HARD: return "hard";
            default: return "medium";
        }
    }

    private TournamentQuestion convertToTournamentQuestion(OpenTDBQuestion apiQuestion) {
        // Find or create the category
        Category category = categoryService.findOrCreateCategory(
                apiQuestion.getCategory(),
                null // We don't know the ID from this call
        );

        return convertToTournamentQuestion(apiQuestion, category);
    }

    private TournamentQuestion convertToTournamentQuestion(OpenTDBQuestion apiQuestion, Category category) {
        TournamentQuestion question = new TournamentQuestion();
        question.setQuestion(apiQuestion.getQuestion());
        question.setCorrectAnswer(apiQuestion.getCorrectAnswer());
        question.setIncorrectAnswers(apiQuestion.getIncorrectAnswers());

        // Set the category
        question.setCategory(category);

        // Determine question type
        question.setType(apiQuestion.getType().equals("boolean") ?
                TournamentQuestion.QuestionType.TRUE_FALSE :
                TournamentQuestion.QuestionType.MULTIPLE_CHOICE);

        return question;
    }

    private Category convertToCategory(OpenTDBCategory apiCategory) {
        Category category = new Category();
        category.setName(apiCategory.getName());
        category.setOpenTdbId(apiCategory.getId());
        return category;
    }

    // DTOs to match OpenTDB API response
    @Data
    private static class OpenTDBResponse {
        private List<OpenTDBQuestion> results;
    }

    @Data
    private static class OpenTDBQuestion {
        private String category;
        private String type;
        private String difficulty;
        private String question;

        @JsonProperty("correct_answer")
        private String correctAnswer;

        @JsonProperty("incorrect_answers")
        private List<String> incorrectAnswers;
    }

    @Data
    private static class OpenTDBCategoryResponse {
        @JsonProperty("trivia_categories")
        private List<OpenTDBCategory> triviaCategories;
    }

    @Data
    private static class OpenTDBCategory {
        private Integer id;
        private String name;
    }
}