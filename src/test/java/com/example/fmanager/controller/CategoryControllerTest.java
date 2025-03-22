package com.example.fmanager.controller;


import com.example.fmanager.dto.CategoryCreateDto;
import com.example.fmanager.dto.CategoryGetDto;
import com.example.fmanager.models.Category;
import com.example.fmanager.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryCreateDto categoryCreateDto;
    private CategoryGetDto categoryGetDto;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryCreateDto = new CategoryCreateDto("Test Category");
        categoryGetDto = new CategoryGetDto();
        categoryGetDto.setId(1);
        categoryGetDto.setName("Test Category");
        category = new Category();
        category.setId(1);
        category.setName("Test Category");
    }

    @Test
    void createCategory_Success() {
        when(categoryService.createCategory(any(CategoryCreateDto.class))).thenReturn(category);
        when(categoryService.findById(1)).thenReturn(Optional.of(categoryGetDto));

        ResponseEntity<CategoryGetDto> response = categoryController.createCategory(categoryCreateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Category", response.getBody().getName());
    }

    @Test
    void createCategory_NotFoundAfterCreation() {
        when(categoryService.createCategory(any(CategoryCreateDto.class))).thenReturn(category);
        when(categoryService.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<CategoryGetDto> response = categoryController.createCategory(categoryCreateDto);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getCategories_ReturnsList() {
        when(categoryService.findAll()).thenReturn(List.of(categoryGetDto));

        List<CategoryGetDto> result = categoryController.getCategories();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());
    }

    @Test
    void getCategoryById_Found() {
        when(categoryService.findById(1)).thenReturn(Optional.of(categoryGetDto));

        ResponseEntity<CategoryGetDto> response = categoryController.getCategoryById(1);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Category", response.getBody().getName());
    }

    @Test
    void getCategoryById_NotFound() {
        when(categoryService.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<CategoryGetDto> response = categoryController.getCategoryById(1);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void deleteCategory_Success() {
        doNothing().when(categoryService).deleteCategory(1);

        assertDoesNotThrow(() -> categoryController.deleteCategory(1));
        verify(categoryService, times(1)).deleteCategory(1);
    }

    @Test
    void updateCategory_Success() {
        when(categoryService.updateCategory(eq(1), any(CategoryCreateDto.class))).thenReturn(categoryGetDto);

        ResponseEntity<CategoryGetDto> response = categoryController.updateCategory(1, categoryCreateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Category", response.getBody().getName());
    }
}