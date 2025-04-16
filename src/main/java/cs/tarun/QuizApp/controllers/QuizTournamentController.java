package cs.tarun.QuizApp.controllers;
import cs.tarun.QuizApp.models.QuizTournament;
import cs.tarun.QuizApp.dto.QuizTournamentDTO;
import cs.tarun.QuizApp.services.QuizTournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class QuizTournamentController {
    // Service layer for tournament operations
    private final QuizTournamentService tournamentService;

    // Endpoint to create a new tournament
    @PostMapping
    public ResponseEntity<QuizTournament> createTournament(@Valid @RequestBody QuizTournamentDTO tournamentDTO) {
        // Create tournament and return with 200 OK status
        QuizTournament createdTournament = tournamentService.createTournament(tournamentDTO);
        return ResponseEntity.ok(createdTournament);
    }

    // Endpoint to get a tournament by ID
    @GetMapping("/{id}")
    public ResponseEntity<QuizTournament> getTournamentById(@PathVariable Long id) {
        QuizTournament tournament = tournamentService.getTournamentById(id);
        return ResponseEntity.ok(tournament);
    }

    // Endpoint to retrieve upcoming tournaments
    @GetMapping("/upcoming")
    public ResponseEntity<List<QuizTournament>> getUpcomingTournaments() {
        return ResponseEntity.ok(tournamentService.getUpcomingTournaments());
    }

    // Endpoint to retrieve ongoing tournaments
    @GetMapping("/ongoing")
    public ResponseEntity<List<QuizTournament>> getOngoingTournaments() {
        return ResponseEntity.ok(tournamentService.getOngoingTournaments());
    }

    // Endpoint to retrieve past tournaments
    @GetMapping("/past")
    public ResponseEntity<List<QuizTournament>> getPastTournaments() {
        return ResponseEntity.ok(tournamentService.getPastTournaments());
    }

    // Endpoint to get all tournaments
    @GetMapping
    public ResponseEntity<List<QuizTournament>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    // Endpoint to update an existing tournament
    @PutMapping("/{id}")
    public ResponseEntity<QuizTournament> updateTournament(
            @PathVariable Long id,
            @Valid @RequestBody QuizTournamentDTO tournamentDTO
    ) {
        // Update tournament and return updated tournament
        QuizTournament updatedTournament = tournamentService.updateTournament(id, tournamentDTO);
        return ResponseEntity.ok(updatedTournament);
    }

    // Endpoint to delete a tournament
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        // Delete tournament and return no content status
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to like a tournament
    @PostMapping("/{id}/like")
    public ResponseEntity<QuizTournament> likeTournament(@PathVariable Long id) {
        // Add a like to the tournament and return updated tournament
        QuizTournament tournament = tournamentService.likeTournament(id);
        return ResponseEntity.ok(tournament);
    }
}