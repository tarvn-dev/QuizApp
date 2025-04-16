package cs.tarun.QuizApp.db;

import cs.tarun.QuizApp.models.Player;
import cs.tarun.QuizApp.models.QuizTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUsername(String username);

    @Query("SELECT p.participatedTournaments FROM Player p WHERE p.id = :playerId")
    List<QuizTournament> findParticipatedTournaments(Long playerId);

    @Query("SELECT p.likedTournaments FROM Player p WHERE p.id = :playerId")
    List<QuizTournament> findLikedTournaments(Long playerId);
}