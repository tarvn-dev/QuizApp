package cs.tarun.QuizApp.controllers;


import cs.tarun.QuizApp.config.JwtUtil;
import cs.tarun.QuizApp.models.QuizParticipation;
import cs.tarun.QuizApp.db.QuizParticipationRepository;
import cs.tarun.QuizApp.services.QuizParticipationService;
import cs.tarun.QuizApp.dto.PlayerQuestionAnswerDTO;
import cs.tarun.QuizApp.dto.PlayerQuestionResponseDTO;
import cs.tarun.QuizApp.dto.PlayerScoreDTO;
import cs.tarun.QuizApp.models.Player;
import cs.tarun.QuizApp.models.PlayerScore;
import cs.tarun.QuizApp.models.QuizTournament;
import cs.tarun.QuizApp.models.TournamentQuestion;
import cs.tarun.QuizApp.services.PlayerService;
import cs.tarun.QuizApp.services.QuizTournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerService playerService;
    private final QuizTournamentService tournamentService;
    private final QuizParticipationService quizParticipationService;
    private final QuizParticipationRepository participationRepository;
    private final JwtUtil jwtUtil;


    @GetMapping("/tournaments/ongoing")
    public ResponseEntity<List<QuizTournament>> getOngoingTournaments() {
        return ResponseEntity.ok(tournamentService.getOngoingTournaments());
    }

    @GetMapping("/tournaments/upcoming")
    public ResponseEntity<List<QuizTournament>> getUpcomingTournaments() {
        return ResponseEntity.ok(tournamentService.getUpcomingTournaments());
    }

    @GetMapping("/tournaments/past")
    public ResponseEntity<List<QuizTournament>> getPastTournaments() {
        return ResponseEntity.ok(tournamentService.getPastTournaments());
    }

    @GetMapping("/tournaments/participated")
    public ResponseEntity<List<QuizTournament>> getParticipatedTournaments(
            @RequestHeader("Authorization") String token) {
        // Extract username from token
        String username = extractUsernameFromToken(token);

        Player player = playerService.getPlayerByUsername(username);
        List<QuizTournament> tournaments = playerService.getPlayerParticipatedTournaments(player.getId());
        return ResponseEntity.ok(tournaments);
    }

    @PostMapping("/scores")
    public ResponseEntity<PlayerScore> submitScore(
            @RequestHeader("Authorization") String token,
            @RequestBody PlayerScoreDTO scoreDTO) {
        // Extract username from token
        String username = extractUsernameFromToken(token);

        PlayerScore savedScore = playerService.submitTournamentScore(username, scoreDTO);
        return ResponseEntity.ok(savedScore);
    }

    @GetMapping("/scores")
    public ResponseEntity<List<PlayerScore>> getPlayerScores(
            @RequestHeader("Authorization") String token) {
        // Extract username from token
        String username = extractUsernameFromToken(token);

        List<PlayerScore> scores = playerService.getPlayerScores(username);
        return ResponseEntity.ok(scores);
    }

    @PostMapping("/tournaments/{tournamentId}/like")
    public ResponseEntity<QuizTournament> likeTournament(
            @RequestHeader("Authorization") String token,
            @PathVariable Long tournamentId) {
        // Extract username from token
        String username = extractUsernameFromToken(token);

        QuizTournament tournament = playerService.likeTournament(username, tournamentId);
        return ResponseEntity.ok(tournament);
    }

    @DeleteMapping("/tournaments/{tournamentId}/like")
    public ResponseEntity<QuizTournament> unlikeTournament(
            @RequestHeader("Authorization") String token,
            @PathVariable Long tournamentId) {
        // Extract username from token
        String username = extractUsernameFromToken(token);

        QuizTournament tournament = playerService.unlikeTournament(username, tournamentId);
        return ResponseEntity.ok(tournament);
    }

    @PostMapping("/question/answer")
    public ResponseEntity<PlayerQuestionResponseDTO> answerQuestion(
            @RequestHeader("Authorization") String token,
            @RequestBody PlayerQuestionAnswerDTO answerDTO) {

        // Extract username from token
        String username = extractUsernameFromToken(token);

        // Get the tournament and question
        QuizTournament tournament = tournamentService.getTournamentById(answerDTO.getTournamentId());

        // Find the question by ID
        TournamentQuestion question = tournament.getQuestions().stream()
                .filter(q -> q.getId().equals(answerDTO.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Check if the answer is correct
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(answerDTO.getAnswer());

        // Prepare feedback
        String feedback = isCorrect
                ? "Correct! Well done."
                : "Incorrect. Try again next time.";

        PlayerQuestionResponseDTO response = new PlayerQuestionResponseDTO(
                isCorrect,
                question.getCorrectAnswer(),
                feedback
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tournaments/{tournamentId}/start")
    public ResponseEntity<QuizParticipation> startTournament(
            @RequestHeader("Authorization") String token,
            @PathVariable Long tournamentId) {

        String username = extractUsernameFromToken(token);
        QuizParticipation participation = quizParticipationService.startTournament(username, tournamentId);
        return ResponseEntity.ok(participation);
    }

    @GetMapping("/participation/{participationId}/question")
    public ResponseEntity<TournamentQuestion> getCurrentQuestion(
            @RequestHeader("Authorization") String token,
            @PathVariable Long participationId) {

        QuizParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        // Verify that the request is from the correct player
        String username = extractUsernameFromToken(token);
        Player player = playerService.getPlayerByUsername(username);

        if (!participation.getPlayer().getId().equals(player.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        TournamentQuestion question = quizParticipationService.getCurrentQuestion(participation);
        return ResponseEntity.ok(question);
    }

    @PostMapping("/participation/{participationId}/answer")
    public ResponseEntity<PlayerQuestionResponseDTO> answerQuestion(
            @RequestHeader("Authorization") String token,
            @PathVariable Long participationId,
            @RequestBody Map<String, String> answerRequest) {

        // Extract answer from request
        String answer = answerRequest.get("answer");
        if (answer == null) {
            throw new RuntimeException("Answer is required");
        }

        // Get participation
        QuizParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        // Verify that the request is from the correct player
        String username = extractUsernameFromToken(token);
        Player player = playerService.getPlayerByUsername(username);

        if (!participation.getPlayer().getId().equals(player.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Get current question before processing
        TournamentQuestion currentQuestion = quizParticipationService.getCurrentQuestion(participation);

        // Process answer
        boolean isCorrect = quizParticipationService.processAnswer(participation, answer);

        // Create response
        PlayerQuestionResponseDTO response = new PlayerQuestionResponseDTO(
                isCorrect,
                currentQuestion.getCorrectAnswer(),
                isCorrect ? "Correct! Well done." : "Incorrect. The correct answer is: " + currentQuestion.getCorrectAnswer()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/participation/{participationId}/result")
    public ResponseEntity<Map<String, Object>> getTournamentResult(
            @RequestHeader("Authorization") String token,
            @PathVariable Long participationId) {

        // Get participation
        QuizParticipation participation = participationRepository.findById(participationId)
                .orElseThrow(() -> new RuntimeException("Participation not found"));

        // Verify that the request is from the correct player
        String username = extractUsernameFromToken(token);
        Player player = playerService.getPlayerByUsername(username);

        if (!participation.getPlayer().getId().equals(player.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Check if the tournament is completed
        if (!participation.isCompleted()) {
            throw new RuntimeException("Tournament is not yet completed");
        }

        // Create result map
        Map<String, Object> result = new HashMap<>();
        result.put("tournamentName", participation.getTournament().getName());
        result.put("score", participation.getCorrectAnswers());
        result.put("totalQuestions", participation.getTournament().getQuestions().size());
        result.put("completed", participation.isCompleted());

        return ResponseEntity.ok(result);
    }

    // Helper method to extract username from token
    private String extractUsernameFromToken(String authHeader) {
        // Assuming token is in format "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUsernameFromToken(token);
        }
        throw new RuntimeException("Invalid or missing Authorization header");
    }
}