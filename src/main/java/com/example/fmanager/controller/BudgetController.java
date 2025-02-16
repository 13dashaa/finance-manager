package com.example.fmanager.controller;

import com.example.fmanager.models.Budget;
import com.example.fmanager.service.BudgetService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/budget")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        budgetService.addBudget(budget);
        return budgetService
                .getBudgetById(budget.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Budget> filterBudgets(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Float limit) {
        return budgetService.filterBudgets(userId, categoryId, limit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudget(@PathVariable int id) {
        return budgetService
                .getBudgetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
