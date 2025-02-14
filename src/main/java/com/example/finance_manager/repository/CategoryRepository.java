package com.example.finance_manager.repository;


import com.example.finance_manager.models.Category;

import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Getter
public  class CategoryRepository{
    private final Map<Integer, Category> categories = new HashMap<>();
    public void addCategory(Category category) {
        if (categories.containsKey(category.getId())) {
            throw new IllegalArgumentException("Budget with id " + category.getId() + " already exists.");
        }
        categories.put(category.getId(),category);
    }
    public List<Category> getCategories() {
        return new ArrayList<>(categories.values());
    }
    public Optional<Category> findCategoryById(Integer id)  {
        return Optional.ofNullable(categories.get(id));
    }
}
