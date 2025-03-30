package com.example.fmanager.controller;

import com.example.fmanager.dto.BudgetGetDto;
import com.example.fmanager.dto.CategoryCreateDto;
import com.example.fmanager.dto.CategoryGetDto;
import com.example.fmanager.service.BudgetService;
import com.example.fmanager.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/categories")
@Tag(name = "Category Management", description = "APIs for managing categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final BudgetService budgetService;

    public CategoryController(CategoryService categoryService, BudgetService budgetService) {
        this.categoryService = categoryService;
        this.budgetService = budgetService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryCreateDto", new CategoryCreateDto(""));
        return "categories/create";
    }

    @PostMapping
    public String createCategory(
            @Valid @ModelAttribute("categoryCreateDto") CategoryCreateDto categoryCreateDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "categories/create";
        }
        categoryService.createCategory(categoryCreateDto);
        return "redirect:/categories";
    }

    @GetMapping
    public String getCategories(Model model) {
        List<CategoryGetDto> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "categories/list";
    }

    @GetMapping("/{id}")
    public String getCategoryById(
            @PathVariable int id,
            Model model
    ) {
        return categoryService
                .findById(id)
                .map(category -> {
                    model.addAttribute("category", category);

                    model.addAttribute("budgetNames",
                            category.getBudgetIds().stream()
                                    .map(budgetId -> budgetService.getBudgetById(budgetId)
                                            .map(BudgetGetDto::getCategoryName)
                                            .orElse("Budget Not Found"))
                                    .toList());
                    CategoryCreateDto categoryUpdateDto = new CategoryCreateDto(category.getName());
                    model.addAttribute("categoryUpdateDto", categoryUpdateDto);

                    return "categories/details";
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found"
                        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Category by ID (API)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Object> deleteCategoryApi(
            @Parameter(description = "ID of the category to delete", example = "1")
            @PathVariable int id
    ) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException rse) {
            if (rse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(
                        "Category not found with id: " + id, HttpStatus.NOT_FOUND
                );
            }
            throw rse;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error: " + e.getMessage(), HttpStatus.BAD_REQUEST
            );
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category by ID (API)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Object> updateCategoryApi(
            @Parameter(description = "ID of the category to update", example = "1")
            @PathVariable int id,
            @Valid @RequestBody CategoryCreateDto categoryUpdateDto, // Принимаем JSON
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(
                    "Validation errors: " + bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST
            );
        }
        try {
            categoryService.updateCategory(id, categoryUpdateDto);
            return ResponseEntity.ok().body("{}");
        } catch (ResponseStatusException rse) {
            if (rse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(
                        "Category not found with id: " + id, HttpStatus.NOT_FOUND
                );
            }
            throw rse;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error: " + e, HttpStatus.BAD_REQUEST
            );
        }
    }
}