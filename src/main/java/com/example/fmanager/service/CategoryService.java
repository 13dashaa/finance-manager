package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.CATEGORY_NOT_FOUND_MESSAGE;

import com.example.fmanager.dto.CategoryDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Category;
import com.example.fmanager.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final InMemoryCache cache;

    public CategoryService(CategoryRepository categoryRepository, InMemoryCache cache) {
        this.categoryRepository = categoryRepository;
        this.cache = cache;
    }

    public List<CategoryDto> findAll() {
        String cacheKey = "all_categories";
        if (cache.containsKey(cacheKey)) {
            return (List<CategoryDto>) cache.get(cacheKey);
        }
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : categories) {
            categoryDtos.add(CategoryDto.convertToDto(category));
        }
        cache.put(cacheKey, categoryDtos);
        return categoryDtos;
    }

    public Optional<CategoryDto> findById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(CATEGORY_NOT_FOUND_MESSAGE));
        return Optional.of(CategoryDto.convertToDto(category));
    }

    public Category createCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        clearCategoryCache();
        return savedCategory;
    }

    @Transactional
    public CategoryDto updateCategory(int id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(CATEGORY_NOT_FOUND_MESSAGE));
        category.setName(categoryDetails.getName());
        category.setBudgets(categoryDetails.getBudgets());
        category.setTransactions(categoryDetails.getTransactions());
        Category savedCategory = categoryRepository.save(category);
        clearCategoryCache();
        return CategoryDto.convertToDto(savedCategory);
    }

    @Transactional
    public void deleteCategory(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(CATEGORY_NOT_FOUND_MESSAGE));
        clearCategoryCache();
        categoryRepository.delete(category);
    }

    public void clearCategoryCache() {
        String cacheKey = "all_categories";
        cache.remove(cacheKey);
    }
}
