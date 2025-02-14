package com.example.finance_manager.service;

import com.example.finance_manager.models.Category;
import com.example.finance_manager.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
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
