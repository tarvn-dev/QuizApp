// QuizParticipation.java
package cs.tarun.QuizApp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quiz_participations")
public class QuizParticipation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @JsonBackReference("tournament-participations")
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private QuizTournament tournament;

    @Column(nullable = false)
    private LocalDateTime participationDate = LocalDateTime.now();

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "current_question_index")
    private int currentQuestionIndex = 0;

    @Column(name = "correct_answers")
    private int correctAnswers = 0;
}