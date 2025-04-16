package cs.tarun.QuizApp.models;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quiz_tournaments")
public class QuizTournament {
    // Primary key with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tournament name with validation to ensure it's not blank
    @NotBlank(message = "Tournament name is required")
    @Column(nullable = false)
    private String name;

    // Quiz category - replace String with entity relationship
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Difficulty level of the tournament (enum)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    // Start date of the tournament (must be in the future)
    // Temporarily commented out to test retrieving past and ongoing tournaments
    //@Future(message = "Start date must be in the future")
    @Column(nullable = false)
    private LocalDateTime startDate;

    // End date of the tournament (must be in the future)
    // Temporarily commented out to test retrieving past and ongoing tournaments
    //@Future(message = "End date must be in the future")
    @Column(nullable = false)
    private LocalDateTime endDate;

    // Number of likes for the tournament, defaulted to 0
    @Column(nullable = false)
    private int likes = 0;

    // One-to-many relationship with tournament questions
    @JsonManagedReference
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TournamentQuestion> questions;

    // One-to-many relationship with quiz participation
    @JsonManagedReference("tournament-participations")
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizParticipation> participations = new ArrayList<>();

    // Current status of the tournament
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    // Enum for tournament difficulty levels
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    // Enum for tournament status tracking
    public enum TournamentStatus {
        UPCOMING,   // Tournament hasn't started
        ONGOING,    // Tournament is currently active
        COMPLETED,  // Tournament has finished
        PAST        // Tournament is no longer active
    }
}