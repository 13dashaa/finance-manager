package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.BUDGET_NOT_FOUND_MESSAGE;

import com.example.fmanager.dto.BudgetDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Budget;
import com.example.fmanager.repository.BudgetRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
    private BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public List<BudgetDto> getAllBudgets() {
        List<Budget> budgets = budgetRepository.findAll();
        List<BudgetDto> budgetDtos = new ArrayList<>();
        for (Budget budget : budgets) {
            budgetDtos.add(BudgetDto.convertToDto(budget));
        }
        return budgetDtos;
    }

    public Optional<BudgetDto> getBudgetById(int id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(BUDGET_NOT_FOUND_MESSAGE));
        return Optional.of(BudgetDto.convertToDto(budget));
    }

    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    @Transactional
    public BudgetDto updateBudget(int id, Budget budgetDetails) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(BUDGET_NOT_FOUND_MESSAGE));
        budget.setPeriod(budgetDetails.getPeriod());
        budget.setLimitation(budgetDetails.getLimitation());
        budget.setClients(budgetDetails.getClients());
        budget.setCategory(budgetDetails.getCategory());
        return BudgetDto.convertToDto(budgetRepository.save(budget));
    }

    @Transactional
    public void deleteBudget(int id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(BUDGET_NOT_FOUND_MESSAGE));
        budgetRepository.delete(budget);
    }
}
