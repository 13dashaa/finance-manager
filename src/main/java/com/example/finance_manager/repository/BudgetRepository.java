package com.example.finance_manager.repository;

import com.example.finance_manager.models.Budget;

import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Getter
public class BudgetRepository{
    private final Map<Integer, Budget> budgets = new HashMap<>();
    public void addBudget(Budget budget) {
        if (budgets.containsKey(budget.getId())) {
            throw new IllegalArgumentException("Budget with id " + budget.getId() + " already exists.");
        }
        budgets.put(budget.getId(), budget);
    }

    public Optional<Budget> findBudgetById(Integer id) {
        return Optional.ofNullable(budgets.get(id)); // Оборачиваем результат в Optional
    }
    public List<Budget> filterBudgets(Integer userId, Integer categoryId, Float limit) {
        return budgets.values().stream()
                .filter(budget -> (userId == null || budget.getUserId() == userId))
                .filter(budget -> (categoryId == null || budget.getCategoryId() == categoryId))
                .filter(budget -> (limit == null || limit.equals(budget.getLimit())))
                .collect(Collectors.toList());
    }
    public List<Budget> getBudgets() {
        return new ArrayList<>(budgets.values());
    }

}
