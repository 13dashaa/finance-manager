package com.example.fmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.fmanager.dto.CategoryDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Categories;
import com.example.fmanager.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;


    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> findAll() {
        List<Categories> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Categories category : categories) {
            categoryDtos.add(CategoryDto.convertToDto(category));
        }
        return categoryDtos;
    }

    public Optional<CategoryDto> findById(int id) {
        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound("Category is not found"));
        return Optional.of(CategoryDto.convertToDto(category));
    }

    public Categories createCategory(Categories category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public CategoryDto updateCategory(int id, Categories categoryDetails) {
        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound("Category not found"));
        category.setName(categoryDetails.getName());
        category.setBudgets(categoryDetails.getBudgets());
        category.setTransactions(categoryDetails.getTransactions());
        return CategoryDto.convertToDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(int id) {
        Categories category = categoryRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound("Category not found"));
        categoryRepository.delete(category);
    }
}
