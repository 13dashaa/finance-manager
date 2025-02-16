package com.example.fmanager.repository;

import com.example.fmanager.models.Category;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
@Getter
public class CategoryRepository {
    private final Map<Integer, Category> categories = new HashMap<>();

    public void addCategory(Category category) {
        if (categories.containsKey(category.getId())) {
            throw new IllegalArgumentException("Budget with id " + category.getId() + " exists.");
        }
        categories.put(category.getId(), category);
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories.values());
    }

    public Optional<Category> findCategoryById(Integer id) {
        return Optional.ofNullable(categories.get(id));
    }
}
