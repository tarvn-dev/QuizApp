package cs.tarun.QuizApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
public class Category {
    // Primary key with auto-increment strategy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the category
    @NotBlank(message = "Category name is required")
    @Column(nullable = false, unique = true)
    private String name;

    // OpenTDB API category ID for reference
    private Integer openTdbId;

    // Description of the category
    @Column(length = 500)
    private String description;
}
