package com.example.fmanager.controller;

import com.example.fmanager.dto.*;
import com.example.fmanager.models.Budget;
import com.example.fmanager.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BudgetController budgetController;

    private BudgetCreateDto budgetCreateDto;
    private BudgetGetDto budgetGetDto;
    private BudgetUpdateDto budgetUpdateDto;
    private Budget budget;

    @BeforeEach
    void setUp() {
        budgetCreateDto = new BudgetCreateDto(12, 5000.0, 1, Set.of(1, 2));
        budgetGetDto = new BudgetGetDto();
        budgetUpdateDto = new BudgetUpdateDto(24, 10000.0, new HashSet<>(List.of(1)));
        budget = new Budget();
        budget.setId(1);
    }

    @Test
    void createBudget_Success() {
        when(budgetService.createBudget(any(BudgetCreateDto.class))).thenReturn(budget);
        when(budgetService.getBudgetById(1)).thenReturn(Optional.of(budgetGetDto));

        ResponseEntity<BudgetGetDto> response = budgetController.createBudget(budgetCreateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getAllBudgets_ReturnsList() {
        when(budgetService.getAllBudgets()).thenReturn(List.of(budgetGetDto));

        List<BudgetGetDto> result = budgetController.getAllBudgets();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getBudget_Found() {
        when(budgetService.getBudgetById(1)).thenReturn(Optional.of(budgetGetDto));

        ResponseEntity<BudgetGetDto> response = budgetController.getBudget(1);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getBudget_NotFound() {
        when(budgetService.getBudgetById(1)).thenReturn(Optional.empty());

        ResponseEntity<BudgetGetDto> response = budgetController.getBudget(1);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void updateBudget_Success() {
        when(budgetService.updateBudget(eq(1), any(BudgetUpdateDto.class))).thenReturn(budgetGetDto);

        ResponseEntity<BudgetGetDto> response = budgetController.updateBudget(1, budgetUpdateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteBudget_Success() {
        doNothing().when(budgetService).deleteBudget(1);

        assertDoesNotThrow(() -> budgetController.deleteBudget(1));
        verify(budgetService, times(1)).deleteBudget(1);
    }
}