package cs.tarun.QuizApp.controllers;

import cs.tarun.QuizApp.dto.QuizTournamentDTO;
import cs.tarun.QuizApp.models.QuizTournament;
import cs.tarun.QuizApp.services.QuizTournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final QuizTournamentService tournamentService;

    @GetMapping("/tournaments")
    public ResponseEntity<List<QuizTournament>> getAllTournaments() {
        // Get all tournaments from repository
        List<QuizTournament> allTournaments = tournamentService.getAllTournaments();
        return ResponseEntity.ok(allTournaments);
    }

    @PostMapping("/tournaments")
    public ResponseEntity<QuizTournament> createTournament(@Valid @RequestBody QuizTournamentDTO tournamentDTO) {
        // Delegate to service to create tournament
        QuizTournament createdTournament = tournamentService.createTournament(tournamentDTO);
        return ResponseEntity.ok(createdTournament);
    }

    @GetMapping("/tournaments/{id}")
    public ResponseEntity<QuizTournament> getTournamentById(@PathVariable Long id) {
        // Get tournament by ID
        QuizTournament tournament = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(tournament);
    }

    @PutMapping("/tournaments/{id}")
    public ResponseEntity<QuizTournament> updateTournament(
            @PathVariable Long id,
            @Valid @RequestBody QuizTournamentDTO tournamentDTO) {
        // Update tournament
        QuizTournament updatedTournament = tournamentService.updateTournament(id, tournamentDTO);
        return ResponseEntity.ok(updatedTournament);
    }

    @DeleteMapping("/tournaments/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        // Delete tournament
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tournaments/{id}/likes")
    public ResponseEntity<Map<String, Integer>> getTournamentLikes(@PathVariable Long id) {
        // Get tournament by ID
        QuizTournament tournament = tournamentService.getTournamentById(id);

        // Return likes count
        Map<String, Integer> response = new HashMap<>();
        response.put("likes", tournament.getLikes());

        return ResponseEntity.ok(response);
    }
}