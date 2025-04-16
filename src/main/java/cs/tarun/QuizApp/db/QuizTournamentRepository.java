package cs.tarun.QuizApp.db;
import cs.tarun.QuizApp.models.QuizTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizTournamentRepository extends JpaRepository<QuizTournament, Long> {
    // Custom query to find tournaments that are scheduled to start in the future
    @Query("SELECT qt FROM QuizTournament qt WHERE qt.startDate > :now")
    List<QuizTournament> findUpcomingTournaments(LocalDateTime now);

    // Custom query to find tournaments currently in progress
    @Query("SELECT qt FROM QuizTournament qt WHERE qt.startDate <= :now AND qt.endDate >= :now")
    List<QuizTournament> findOngoingTournaments(LocalDateTime now);

    // Custom query to find tournaments that have already ended
    @Query("SELECT qt FROM QuizTournament qt WHERE qt.endDate < :now")
    List<QuizTournament> findPastTournaments(LocalDateTime now);
}
