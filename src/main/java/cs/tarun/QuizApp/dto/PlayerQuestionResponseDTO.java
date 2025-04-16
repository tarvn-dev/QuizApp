package cs.tarun.QuizApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerQuestionResponseDTO {
    private boolean correct;
    private String correctAnswer;
    private String feedback;
}