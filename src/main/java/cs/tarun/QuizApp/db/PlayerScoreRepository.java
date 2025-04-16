package cs.tarun.QuizApp.db;

import cs.tarun.QuizApp.models.Player;
import cs.tarun.QuizApp.models.PlayerScore;
import cs.tarun.QuizApp.models.QuizTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerScoreRepository extends JpaRepository<PlayerScore, Long> {
    List<PlayerScore> findByPlayer(Player player);
    List<PlayerScore> findByTournament(QuizTournament tournament);
    Optional<PlayerScore> findByPlayerAndTournament(Player player, QuizTournament tournament);
}