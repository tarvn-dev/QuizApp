package cs.tarun.QuizApp.dto;

import lombok.Data;

@Data
public class PlayerScoreDTO {
    private Long tournamentId;
    private int score;
    private int correctAnswers;
    private int totalQuestions;
    private long completionTimeInSeconds;
}