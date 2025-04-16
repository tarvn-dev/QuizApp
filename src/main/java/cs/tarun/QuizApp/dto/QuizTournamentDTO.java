package cs.tarun.QuizApp.dto;

import cs.tarun.QuizApp.models.QuizTournament;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuizTournamentDTO {
    // Tournament name
    @NotBlank(message = "Tournament name is required")
    private String name;

    // Category ID for simplified API interaction
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    // Tournament difficulty level
    @NotNull(message = "Difficulty is required")
    private QuizTournament.Difficulty difficulty;

    // Start date for the tournament
    @NotNull(message = "Start date is required")
    // Temporarily commented out to test retrieving past and ongoing tournaments
    //@Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    // End date for the tournament
    @NotNull(message = "End date is required")
    // Temporarily commented out to test retrieving past and ongoing tournaments
    //@Future(message = "End date must be in the future")
    private LocalDateTime endDate;
}
