package com.example.fmanager.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.fmanager.models.Budgets;
import com.example.fmanager.models.Categories;
import com.example.fmanager.models.Transactions;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {

    private int id;
    private String name;
    private Set<Integer> budgetIds;
    private Set<Integer> transactionIds;


    public static CategoryDto convertToDto(Categories category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        Set<Budgets> budgets = category.getBudgets();
        if (budgets != null) {
            dto.setBudgetIds(budgets.stream().map(Budgets::getId).collect(Collectors.toSet()));
        } else {
            dto.setBudgetIds(new HashSet<>());
        }
        Set<Transactions> transactions = category.getTransactions();
        if (transactions != null) {
            dto.setTransactionIds(category.getTransactions().stream()
                    .map(Transactions::getId)
                    .collect(Collectors.toSet()));
        } else {
            dto.setTransactionIds(new HashSet<>());
        }
        dto.setName(category.getName());

        return dto;
    }
}