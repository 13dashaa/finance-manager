package com.example.fmanager.controller;

import com.example.fmanager.dto.TransactionCreateDto;
import com.example.fmanager.dto.TransactionGetDto;
import com.example.fmanager.models.Transaction;
import com.example.fmanager.models.Account;
import com.example.fmanager.models.Category;
import com.example.fmanager.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionCreateDto transactionCreateDto;
    private TransactionGetDto transactionGetDto;
    private Transaction transaction;
    private Account account;
    private Category category;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1);
        account.setName("Test Account");

        category = new Category();
        category.setId(1);
        category.setName("Test Category");

        transactionCreateDto = new TransactionCreateDto(
                "Test Transaction",
                100.0,
                LocalDateTime.of(2023, 10, 1, 12, 0),
                1,
                1
        );

        transactionGetDto = new TransactionGetDto();
        transactionGetDto.setId(1);
        transactionGetDto.setDescription("Test Transaction");
        transactionGetDto.setAmount(100.0);
        transactionGetDto.setAccountId(1);
        transactionGetDto.setCategoryId(1);
        transactionGetDto.setDate(LocalDateTime.of(2023, 10, 1, 12, 0));
        transactionGetDto.setCreatedAt(LocalDateTime.of(2023, 10, 1, 12, 0));

        transaction = new Transaction();
        transaction.setId(1);
        transaction.setDescription("Test Transaction");
        transaction.setAmount(100.0);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setDate(LocalDateTime.of(2023, 10, 1, 12, 0));
        transaction.setCreatedAt(LocalDateTime.of(2023, 10, 1, 12, 0));
    }

    @Test
    void createTransaction_Success() {
        when(transactionService.createTransaction(any(TransactionCreateDto.class))).thenReturn(transaction);
        when(transactionService.getTransactionById(1)).thenReturn(Optional.of(transactionGetDto));

        ResponseEntity<TransactionGetDto> response = transactionController.createTransaction(transactionCreateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Transaction", response.getBody().getDescription());
        assertEquals(100.0, response.getBody().getAmount());
        assertEquals(1, response.getBody().getAccountId());
        assertEquals(1, response.getBody().getCategoryId());
        assertEquals(LocalDateTime.of(2023, 10, 1, 12, 0), response.getBody().getDate());
    }

    @Test
    void createTransaction_NotFoundAfterCreation() {
        when(transactionService.createTransaction(any(TransactionCreateDto.class))).thenReturn(transaction);
        when(transactionService.getTransactionById(1)).thenReturn(Optional.empty());

        ResponseEntity<TransactionGetDto> response = transactionController.createTransaction(transactionCreateDto);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getTransactions_ReturnsList() {
        when(transactionService.getAllTransactions()).thenReturn(List.of(transactionGetDto));

        List<TransactionGetDto> result = transactionController.getTransactions();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Transaction", result.get(0).getDescription());
        assertEquals(100.0, result.get(0).getAmount());
        assertEquals(1, result.get(0).getAccountId());
        assertEquals(1, result.get(0).getCategoryId());
    }

    @Test
    void getTransaction_Found() {
        when(transactionService.getTransactionById(1)).thenReturn(Optional.of(transactionGetDto));

        ResponseEntity<TransactionGetDto> response = transactionController.getTransaction(1);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Transaction", response.getBody().getDescription());
        assertEquals(100.0, response.getBody().getAmount());
        assertEquals(1, response.getBody().getAccountId());
        assertEquals(1, response.getBody().getCategoryId());
    }

    @Test
    void getTransaction_NotFound() {
        when(transactionService.getTransactionById(1)).thenReturn(Optional.empty());

        ResponseEntity<TransactionGetDto> response = transactionController.getTransaction(1);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getTransactionsByClientAndCategory_Success() {
        when(transactionService.findByClientIdAndCategoryId(1, 1)).thenReturn(List.of(transactionGetDto));

        List<TransactionGetDto> result = transactionController.getTransactionsByClientAndCategory(1, 1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Transaction", result.get(0).getDescription());
        assertEquals(100.0, result.get(0).getAmount());
        assertEquals(1, result.get(0).getAccountId());
        assertEquals(1, result.get(0).getCategoryId());
    }

    @Test
    void updateTransaction_Success() {
        when(transactionService.updateTransaction(eq(1), any(TransactionCreateDto.class))).thenReturn(transactionGetDto);

        ResponseEntity<TransactionGetDto> response = transactionController.updateTransaction(1, transactionCreateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Transaction", response.getBody().getDescription());
        assertEquals(100.0, response.getBody().getAmount());
        assertEquals(1, response.getBody().getAccountId());
        assertEquals(1, response.getBody().getCategoryId());
    }

    @Test
    void deleteTransaction_Success() {
        doNothing().when(transactionService).deleteTransaction(1);

        assertDoesNotThrow(() -> transactionController.deleteTransaction(1));
        verify(transactionService, times(1)).deleteTransaction(1);
    }
}