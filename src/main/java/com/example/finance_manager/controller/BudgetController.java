package com.example.finance_manager.controller;

import com.example.finance_manager.models.Budget;
import com.example.finance_manager.service.BudgetService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
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
        return budgetService.getBudgetById(budget.getId()).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public List<Budget> filterBudgets(@RequestParam(required = false) Integer userId,
                                      @RequestParam(required = false) Integer categoryId,
                                      @RequestParam(required = false) Float limit) {
        return  budgetService.filterBudgets(userId, categoryId, limit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudget(@PathVariable int id) {
        return budgetService.getBudgetById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
