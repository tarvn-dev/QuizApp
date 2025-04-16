package cs.tarun.QuizApp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "players")
public class Player extends User {
    @Column(nullable = false)
    private int score = 0;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "player_participated_tournaments",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "tournament_id")
    )
    private Set<QuizTournament> participatedTournaments = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "player_liked_tournaments",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "tournament_id")
    )
    private Set<QuizTournament> likedTournaments = new HashSet<>();

    public Player(String username, String password, String email, String fullName) {
        super(null, username, password, email, fullName, UserRole.PLAYER);
    }
}