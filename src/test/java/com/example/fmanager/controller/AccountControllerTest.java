package com.example.fmanager.controller;

import com.example.fmanager.dto.*;
import com.example.fmanager.models.Account;
import com.example.fmanager.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private AccountCreateDto accountCreateDto;
    private AccountGetDto accountGetDto;
    private AccountUpdateDto accountUpdateDto;
    private Account account;

    @BeforeEach
    void setUp() {
        accountCreateDto = new AccountCreateDto("Test Account", 100.0, 1);
        accountGetDto = new AccountGetDto();
        accountUpdateDto = new AccountUpdateDto("Updated Account", 200.0);
        account = new Account();
        account.setId(1);
    }

    @Test
    void createAccount_Success() {
        when(accountService.createAccount(any(AccountCreateDto.class))).thenReturn(account);
        when(accountService.getAccountById(1)).thenReturn(Optional.of(accountGetDto));

        ResponseEntity<AccountGetDto> response = accountController.createAccount(accountCreateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getAccounts_ReturnsList() {
        when(accountService.getAllAccounts()).thenReturn(List.of(accountGetDto));

        List<AccountGetDto> result = accountController.getAccounts();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAccount_Found() {
        when(accountService.getAccountById(1)).thenReturn(Optional.of(accountGetDto));

        ResponseEntity<AccountGetDto> response = accountController.getAccount(1);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getAccount_NotFound() {
        when(accountService.getAccountById(1)).thenReturn(Optional.empty());

        ResponseEntity<AccountGetDto> response = accountController.getAccount(1);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getAccountsByClient_Success() {
        when(accountService.findByClientId(1)).thenReturn(List.of(accountGetDto));

        List<AccountGetDto> result = accountController.getAccountsByClient(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void updateAccount_Success() {
        when(accountService.updateAccount(eq(1), any(AccountUpdateDto.class))).thenReturn(accountGetDto);

        ResponseEntity<AccountGetDto> response = accountController.updateAccount(1, accountUpdateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void createAccountsBulk_Success() {
        BulkCreateDto<AccountCreateDto> bulkCreateDto = new BulkCreateDto<>(
                List.of(new AccountCreateDto(
                                "Test Account",
                                100.0,
                                1))
        );
        when(accountService.createAccount(any(AccountCreateDto.class))).thenReturn(account);
        when(accountService.getAccountById(1)).thenReturn(Optional.of(accountGetDto));

        ResponseEntity<List<AccountGetDto>> response = accountController.createAccountsBulk(bulkCreateDto);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteAccount_Success() {
        doNothing().when(accountService).deleteAccount(1);

        assertDoesNotThrow(() -> accountController.deleteAccount(1));
        verify(accountService, times(1)).deleteAccount(1);
    }
}