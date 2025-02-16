package com.example.fmanager.service;

import com.example.fmanager.models.Budget;
import com.example.fmanager.repository.BudgetRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    private BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public void addBudget(Budget budget) {
        budgetRepository.addBudget(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.getBudgets();
    }

    public Optional<Budget> getBudgetById(int id) {
        return budgetRepository.findBudgetById(id);
    }

    public List<Budget> filterBudgets(Integer userId, Integer categoryId, Float limit) {
        return budgetRepository.filterBudgets(userId, categoryId, limit);
    }
}
