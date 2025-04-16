
package cs.tarun.QuizApp.dto;

import lombok.Data;

@Data
public class PlayerQuestionAnswerDTO {
    private Long tournamentId;
    private Long questionId;
    private String answer;
}