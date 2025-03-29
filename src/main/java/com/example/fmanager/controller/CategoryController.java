package com.example.fmanager.controller;

import com.example.fmanager.dto.CategoryCreateDto;
import com.example.fmanager.dto.CategoryGetDto;
import com.example.fmanager.models.Category;
import com.example.fmanager.service.BudgetService; // Import BudgetService
import com.example.fmanager.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/categories")
@Tag(name = "Category Management", description = "APIs for managing categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final BudgetService budgetService; // Add BudgetService

    public CategoryController(CategoryService categoryService, BudgetService budgetService) { // Inject BudgetService
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

                    // Fetch budget names for display in the view
                    model.addAttribute("budgetNames",
                            category.getBudgetIds().stream()
                                    .map(budgetId -> budgetService.getBudgetById(budgetId)
                                            .map(budget -> budget.getCategoryName()) // Assuming CategoryName is the right field. Change if needed.
                                            .orElse("Budget Not Found"))
                                    .toList()); // Get budget names

                    return "categories/details";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable int id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable int id, Model model) {
        return categoryService.findById(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    model.addAttribute("categoryCreateDto", new CategoryCreateDto(category.getName()));
                    return "categories/edit";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(
            @PathVariable int id,
            @Valid @ModelAttribute("categoryCreateDto") CategoryCreateDto categoryDetails,
            BindingResult bindingResult,
            Model model) {
        CategoryGetDto category = categoryService.findById(id).orElse(null);

        if (bindingResult.hasErrors() || category == null) {
            model.addAttribute("category", category);
            return "categories/edit";
        }
        categoryService.updateCategory(id, categoryDetails);
        return "redirect:/categories/" + id;
    }
}