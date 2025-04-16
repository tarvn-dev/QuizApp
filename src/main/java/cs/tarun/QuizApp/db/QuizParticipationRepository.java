package cs.tarun.QuizApp.db;

import cs.tarun.QuizApp.models.Player;
import cs.tarun.QuizApp.models.QuizParticipation;
import cs.tarun.QuizApp.models.QuizTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizParticipationRepository extends JpaRepository<QuizParticipation, Long> {
    List<QuizParticipation> findByPlayer(Player player);
    List<QuizParticipation> findByTournament(QuizTournament tournament);
    Optional<QuizParticipation> findByPlayerAndTournamentAndCompletedFalse(Player player, QuizTournament tournament);
}