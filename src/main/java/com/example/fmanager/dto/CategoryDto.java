package com.example.fmanager.dto;

import com.example.fmanager.models.Budget;
import com.example.fmanager.models.Category;
import com.example.fmanager.models.Transaction;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {

    private int id;
    private String name;
    private Set<Integer> budgetIds;
    private Set<Integer> transactionIds;

    public static CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        Set<Budget> budgets = category.getBudgets();
        if (budgets != null) {
            dto.setBudgetIds(budgets.stream().map(Budget::getId).collect(Collectors.toSet()));
        } else {
            dto.setBudgetIds(new HashSet<>());
        }
        Set<Transaction> transactions = category.getTransactions();
        if (transactions != null) {
            dto.setTransactionIds(category.getTransactions().stream()
                    .map(Transaction::getId)
                    .collect(Collectors.toSet()));
        } else {
            dto.setTransactionIds(new HashSet<>());
        }
        dto.setName(category.getName());
        return dto;
    }
}