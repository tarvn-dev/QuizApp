package cs.tarun.QuizApp.services;

import cs.tarun.QuizApp.models.Category;
import cs.tarun.QuizApp.models.QuizTournament;
import cs.tarun.QuizApp.models.TournamentQuestion;
import cs.tarun.QuizApp.dto.QuizTournamentDTO;
import cs.tarun.QuizApp.db.QuizTournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class QuizTournamentService {
    private final QuizTournamentRepository tournamentRepository;
    private final OpenTDBService openTDBService;
    private final CategoryService categoryService;

    // Create tournament from DTO
    @Transactional
    public QuizTournament createTournament(QuizTournamentDTO tournamentDTO) {
        // Convert DTO to entity
        QuizTournament tournament = new QuizTournament();
        tournament.setName(tournamentDTO.getName());
        tournament.setDifficulty(tournamentDTO.getDifficulty());
        tournament.setStartDate(tournamentDTO.getStartDate());
        tournament.setEndDate(tournamentDTO.getEndDate());

        // Find category by ID
        Category category = categoryService.getCategoryById(tournamentDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        tournament.setCategory(category);

        // Set tournament status
        tournament.setStatus(determineTournamentStatus(tournament));

        // Save tournament to get its ID
        QuizTournament savedTournament = tournamentRepository.save(tournament);

        // Fetch questions based on tournament difficulty and category
        List<TournamentQuestion> questions = openTDBService.fetchQuestionsByCategory(
                tournament.getDifficulty(),
                category.getId()
        );

        // Set the tournament relationship for each question
        for (TournamentQuestion question : questions) {
            question.setTournament(savedTournament);
        }

        // Set the questions on the tournament
        savedTournament.setQuestions(questions);

        // Save again to persist the relationship
        return tournamentRepository.save(savedTournament);
    }

    // Determine the status of a tournament based on current date
    private QuizTournament.TournamentStatus determineTournamentStatus(QuizTournament tournament) {
        LocalDateTime now = LocalDateTime.now();

        // Check if tournament is upcoming
        if (now.isBefore(tournament.getStartDate())) {
            return QuizTournament.TournamentStatus.UPCOMING;
        }
        // Check if tournament is currently ongoing
        else if (now.isAfter(tournament.getStartDate()) && now.isBefore(tournament.getEndDate())) {
            return QuizTournament.TournamentStatus.ONGOING;
        }
        // Tournament is past
        else {
            return QuizTournament.TournamentStatus.PAST;
        }
    }

    // Retrieve all tournaments
    public List<QuizTournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    // Retrieve upcoming tournaments
    public List<QuizTournament> getUpcomingTournaments() {
        return tournamentRepository.findUpcomingTournaments(LocalDateTime.now());
    }

    // Retrieve ongoing tournaments
    public List<QuizTournament> getOngoingTournaments() {
        return tournamentRepository.findOngoingTournaments(LocalDateTime.now());
    }

    // Retrieve past tournaments
    public List<QuizTournament> getPastTournaments() {
        return tournamentRepository.findPastTournaments(LocalDateTime.now());
    }

    // Update an existing tournament
    @Transactional
    public QuizTournament updateTournament(Long id, QuizTournamentDTO tournamentDTO) {
        // Find existing tournament or throw exception
        QuizTournament existingTournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Update tournament details
        existingTournament.setName(tournamentDTO.getName());
        existingTournament.setStartDate(tournamentDTO.getStartDate());
        existingTournament.setEndDate(tournamentDTO.getEndDate());

        // Update category if it has changed
        Category category = categoryService.getCategoryById(tournamentDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existingTournament.setCategory(category);

        // Update difficulty if needed
        existingTournament.setDifficulty(tournamentDTO.getDifficulty());

        // Update status based on current dates
        existingTournament.setStatus(determineTournamentStatus(existingTournament));

        // Save and return updated tournament
        return tournamentRepository.save(existingTournament);
    }

    // Delete a tournament by its ID
    @Transactional
    public void deleteTournament(Long id) {
        // Find existing tournament or throw exception
        QuizTournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Delete the tournament
        tournamentRepository.delete(tournament);
    }

    // Add a like to a tournament
    @Transactional
    public QuizTournament likeTournament(Long id) {
        // Find existing tournament or throw exception
        QuizTournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Increment likes and save
        tournament.setLikes(tournament.getLikes() + 1);
        return tournamentRepository.save(tournament);
    }

    // Get tournament by ID
    public QuizTournament getTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
    }
}