package cs.tarun.QuizApp.services;

import cs.tarun.QuizApp.db.PlayerScoreRepository;
import cs.tarun.QuizApp.models.PlayerScore;
import cs.tarun.QuizApp.models.QuizTournament;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerScoreService {
    private final PlayerScoreRepository playerScoreRepository;

    public List<PlayerScore> getScoresByTournament(QuizTournament tournament) {
        return playerScoreRepository.findByTournament(tournament);
    }

    public List<PlayerScore> getAllScores() {
        return playerScoreRepository.findAll();
    }
}