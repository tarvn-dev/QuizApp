package cs.tarun.QuizApp.db;

import cs.tarun.QuizApp.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Find a category by name
    Optional<Category> findByName(String name);

    // Find a category by OpenTDB ID
    Optional<Category> findByOpenTdbId(Integer openTdbId);

    // Check if a category exists by name
    boolean existsByName(String name);
}
