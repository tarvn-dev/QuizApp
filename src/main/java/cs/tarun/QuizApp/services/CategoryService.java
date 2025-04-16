package cs.tarun.QuizApp.services;
import cs.tarun.QuizApp.models.Category;
import cs.tarun.QuizApp.db.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get a specific category by ID
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Find a category by name
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    // Find or create a category by name
    @Transactional
    public Category findOrCreateCategory(String name, Integer openTdbId) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(name);
                    newCategory.setOpenTdbId(openTdbId);
                    return categoryRepository.save(newCategory);
                });
    }

    // Create a new category
    @Transactional
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Update an existing category
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingCategory.setName(categoryDetails.getName());
        existingCategory.setDescription(categoryDetails.getDescription());
        existingCategory.setOpenTdbId(categoryDetails.getOpenTdbId());

        return categoryRepository.save(existingCategory);
    }

    // Delete a category
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(category);
    }
}
