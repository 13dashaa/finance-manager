package com.example.fmanager.repository;

import com.example.fmanager.models.Budget;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
@Getter
public class BudgetRepository {
    private final Map<Integer, Budget> budgets = new HashMap<>();

    public void addBudget(Budget budget) {
        if (budgets.containsKey(budget.getId())) {
            throw new
                    IllegalArgumentException("Budget with id " + budget.getId() + " exists.");
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
                .toList(); // Используем Stream.toList() для получения неизменяемого списка
    }

    public List<Budget> getBudgets() {
        return List.copyOf(budgets.values()); // Возвращаем неизменяемый список
    }
}
