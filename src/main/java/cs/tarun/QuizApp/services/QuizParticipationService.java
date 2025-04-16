package cs.tarun.QuizApp.services;

import cs.tarun.QuizApp.db.PlayerRepository;
import cs.tarun.QuizApp.db.QuizParticipationRepository;
import cs.tarun.QuizApp.db.QuizTournamentRepository;
import cs.tarun.QuizApp.models.Player;
import cs.tarun.QuizApp.models.QuizParticipation;
import cs.tarun.QuizApp.models.QuizTournament;
import cs.tarun.QuizApp.models.TournamentQuestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizParticipationService {
    private final QuizParticipationRepository participationRepository;
    private final PlayerRepository playerRepository;
    private final QuizTournamentRepository tournamentRepository;

    public List<QuizParticipation> getPlayerParticipations(Player player) {
        return participationRepository.findByPlayer(player);
    }

    @Transactional
    public QuizParticipation startTournament(String username, Long tournamentId) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        QuizTournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Check if the player already has an ongoing participation
        Optional<QuizParticipation> existingParticipation =
                participationRepository.findByPlayerAndTournamentAndCompletedFalse(player, tournament);

        if (existingParticipation.isPresent()) {
            return existingParticipation.get();
        }

        // Create new participation
        QuizParticipation participation = new QuizParticipation();
        participation.setPlayer(player);
        participation.setTournament(tournament);

        return participationRepository.save(participation);
    }

    @Transactional
    public boolean processAnswer(QuizParticipation participation, String answer) {
        QuizTournament tournament = participation.getTournament();
        int questionIndex = participation.getCurrentQuestionIndex();

        // Ensure we haven't reached the end
        if (questionIndex >= tournament.getQuestions().size()) {
            throw new RuntimeException("No more questions in this tournament");
        }

        // Get current question
        TournamentQuestion currentQuestion = tournament.getQuestions().get(questionIndex);

        // Check if answer is correct
        boolean isCorrect = currentQuestion.getCorrectAnswer().equalsIgnoreCase(answer);

        // Update participation
        if (isCorrect) {
            participation.setCorrectAnswers(participation.getCorrectAnswers() + 1);
        }

        // Move to next question
        participation.setCurrentQuestionIndex(questionIndex + 1);

        // Check if this was the last question
        if (participation.getCurrentQuestionIndex() >= tournament.getQuestions().size()) {
            participation.setCompleted(true);
        }

        participationRepository.save(participation);
        return isCorrect;
    }

    public TournamentQuestion getCurrentQuestion(QuizParticipation participation) {
        QuizTournament tournament = participation.getTournament();
        int questionIndex = participation.getCurrentQuestionIndex();

        // Ensure we haven't reached the end
        if (questionIndex >= tournament.getQuestions().size()) {
            throw new RuntimeException("No more questions in this tournament");
        }

        return tournament.getQuestions().get(questionIndex);
    }
}