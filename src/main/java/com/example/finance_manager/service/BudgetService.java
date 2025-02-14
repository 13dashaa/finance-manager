package com.example.finance_manager.service;

import com.example.finance_manager.models.Budget;
import com.example.finance_manager.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
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
