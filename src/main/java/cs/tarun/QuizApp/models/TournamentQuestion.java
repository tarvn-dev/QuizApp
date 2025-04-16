package cs.tarun.QuizApp.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tournament_questions")
public class TournamentQuestion {
    // Primary key with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The actual question text (with increased length to accommodate complex questions)
    @Column(nullable = false, length = 1000)
    private String question;

    // The correct answer to the question
    @Column(nullable = false)
    private String correctAnswer;

    // List of incorrect answers (for multiple-choice questions)
    @ElementCollection
    @CollectionTable(name = "question_choices", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "choice")
    private List<String> incorrectAnswers;

    // Category of the question
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Type of question (multiple choice or true/false)
    @Enumerated(EnumType.STRING)
    private QuestionType type;

    // Many-to-one relationship with the quiz tournament
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "tournament_id", nullable = false)
    private QuizTournament tournament;

    // Enum to define question types
    public enum QuestionType {
        MULTIPLE_CHOICE,  // Questions with multiple answer options
        TRUE_FALSE        // Binary true/false questions
    }
}