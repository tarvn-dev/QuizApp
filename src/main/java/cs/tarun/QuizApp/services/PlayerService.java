package cs.tarun.QuizApp.services;

import cs.tarun.QuizApp.db.PlayerRepository;
import cs.tarun.QuizApp.db.PlayerScoreRepository;
import cs.tarun.QuizApp.db.QuizTournamentRepository;
import cs.tarun.QuizApp.dto.PlayerScoreDTO;
import cs.tarun.QuizApp.models.Player;
import cs.tarun.QuizApp.models.PlayerScore;
import cs.tarun.QuizApp.models.QuizTournament;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final QuizTournamentRepository tournamentRepository;
    private final PlayerScoreRepository playerScoreRepository;

    public Player getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }

    public List<QuizTournament> getPlayerParticipatedTournaments(Long playerId) {
        return playerRepository.findParticipatedTournaments(playerId);
    }

    public List<QuizTournament> getPlayerLikedTournaments(Long playerId) {
        return playerRepository.findLikedTournaments(playerId);
    }

    @Transactional
    public PlayerScore submitTournamentScore(String username, PlayerScoreDTO scoreDTO) {
        Player player = getPlayerByUsername(username);

        QuizTournament tournament = tournamentRepository.findById(scoreDTO.getTournamentId())
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Add tournament to participated tournaments
        player.getParticipatedTournaments().add(tournament);
        playerRepository.save(player);

        // Create or update player score
        Optional<PlayerScore> existingScoreOpt = playerScoreRepository
                .findByPlayerAndTournament(player, tournament);

        PlayerScore playerScore;
        if (existingScoreOpt.isPresent()) {
            playerScore = existingScoreOpt.get();
            // Only update if new score is better
            if (scoreDTO.getScore() > playerScore.getScore()) {
                playerScore.setScore(scoreDTO.getScore());
                playerScore.setCorrectAnswers(scoreDTO.getCorrectAnswers());
                playerScore.setTotalQuestions(scoreDTO.getTotalQuestions());
                playerScore.setCompletionTimeInSeconds(scoreDTO.getCompletionTimeInSeconds());
            }
        } else {
            playerScore = new PlayerScore();
            playerScore.setPlayer(player);
            playerScore.setTournament(tournament);
            playerScore.setScore(scoreDTO.getScore());
            playerScore.setCorrectAnswers(scoreDTO.getCorrectAnswers());
            playerScore.setTotalQuestions(scoreDTO.getTotalQuestions());
            playerScore.setCompletionTimeInSeconds(scoreDTO.getCompletionTimeInSeconds());
        }

        return playerScoreRepository.save(playerScore);
    }

    @Transactional
    public QuizTournament likeTournament(String username, Long tournamentId) {
        Player player = getPlayerByUsername(username);

        QuizTournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Check if player already liked this tournament
        if (!player.getLikedTournaments().contains(tournament)) {
            player.getLikedTournaments().add(tournament);
            playerRepository.save(player);

            // Increment likes counter
            tournament.setLikes(tournament.getLikes() + 1);
            tournament = tournamentRepository.save(tournament);
        }

        return tournament;
    }

    @Transactional
    public QuizTournament unlikeTournament(String username, Long tournamentId) {
        Player player = getPlayerByUsername(username);

        QuizTournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Check if player has liked this tournament
        if (player.getLikedTournaments().contains(tournament)) {
            player.getLikedTournaments().remove(tournament);
            playerRepository.save(player);

            // Decrement likes counter (ensure it doesn't go below 0)
            tournament.setLikes(Math.max(0, tournament.getLikes() - 1));
            tournament = tournamentRepository.save(tournament);
        }

        return tournament;
    }

    public List<PlayerScore> getPlayerScores(String username) {
        Player player = getPlayerByUsername(username);
        return playerScoreRepository.findByPlayer(player);
    }
}