package com.example.fmanager.service;

import com.example.fmanager.models.Category;
import com.example.fmanager.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void addCategory(Category category) {
        categoryRepository.addCategory(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.getCategories();
    }

    public Optional<Category> getCategoryById(int id) {
        return categoryRepository.findCategoryById(id);
    }
}
