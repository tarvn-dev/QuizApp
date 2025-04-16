package cs.tarun.QuizApp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player_scores")
public class PlayerScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private QuizTournament tournament;

    @Column(nullable = false)
    private int score;

    @Column(name = "correct_answers")
    private int correctAnswers;

    @Column(name = "total_questions")
    private int totalQuestions;

    @Column(name = "completion_time")
    private long completionTimeInSeconds;
}