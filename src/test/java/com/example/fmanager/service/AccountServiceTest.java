package com.example.fmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.fmanager.dto.AccountCreateDto;
import com.example.fmanager.dto.AccountGetDto;
import com.example.fmanager.dto.AccountUpdateDto;
import com.example.fmanager.exception.NotFoundException;
import com.example.fmanager.models.Account;
import com.example.fmanager.models.Client;
import com.example.fmanager.repository.AccountRepository;
import com.example.fmanager.repository.CategoryRepository;
import com.example.fmanager.repository.ClientRepository;
import com.example.fmanager.service.InMemoryCache;
import com.example.fmanager.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private InMemoryCache cache;

    @InjectMocks
    private AccountService accountService;

    private Account account1;
    private Account account2;
    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1);
        client.setUsername("clientuser");
        client.setPassword("clientpass");
        client.setEmail("client@example.com");

        account1 = new Account();
        account1.setId(1);
        account1.setName("Account 1");
        account1.setBalance(1000);
        account1.setClient(client);

        account2 = new Account();
        account2.setId(2);
        account2.setName("Account 2");
        account2.setBalance(2000.);
        account2.setClient(client);
    }

    @Test
    void getAccountById_Success() {
        when(accountRepository.findById(1)).thenReturn(Optional.of(account1));

        Optional<AccountGetDto> result = accountService.getAccountById(1);

        assertTrue(result.isPresent());
        assertEquals("Account 1", result.get().getName());
        assertEquals(1000, result.get().getBalance());
    }

    @Test
    void getAccountById_NotFound() {
        when(accountRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.getAccountById(1));
    }

    @Test
    void getAllAccounts() {
        when(accountRepository.findAll()).thenReturn(Arrays.asList(account1, account2));

        List<AccountGetDto> result = accountService.getAllAccounts();

        assertEquals(2, result.size());
        assertEquals("Account 1", result.get(0).getName());
        assertEquals(1000, result.get(0).getBalance());
        assertEquals("Account 2", result.get(1).getName());
        assertEquals(2000, result.get(1).getBalance());
    }
    @Test
    void findByClientId_Success() {
        String cacheKey = "accounts_client_1";
        when(accountRepository.findAllByClientId(1)).thenReturn(Arrays.asList(account1, account2));
        when(cache.containsKey(cacheKey)).thenReturn(false);

        List<AccountGetDto> result = accountService.findByClientId(1);
        assertEquals(2, result.size());
        assertEquals("Account 1", result.get(0).getName());
        verify(cache, times(1)).put(cacheKey, result);
    }
/*    @Test
    void findByClientId_WithCache() {
        List<Account> accounts = Arrays.asList(account1, account2);
        when(cache.containsKey("accounts_client_1")).thenReturn(true);
        when(cache.get("accounts_client_1")).thenReturn(accounts);

        List<AccountGetDto> result = accountService.findByClientId(1);

        assertEquals(2, result.size());
        assertEquals("Account 1", result.get(0).getName());
        verify(cache, times(1)).get("accounts_client_1");
    }

    @Test
    void findByClientId_WithoutCache() {
        List<Account> accounts = Arrays.asList(account1, account2);
        when(cache.containsKey("accounts_client_1")).thenReturn(false);
        when(accountRepository.findAllByClientId(1)).thenReturn(accounts);

        List<AccountGetDto> result = accountService.findByClientId(1);

        assertEquals(2, result.size());
        assertEquals("Account 1", result.get(0).getName());
        verify(cache, times(1)).put("accounts_client_1", accounts);
    }*/

    @Test
    void createAccount_Success() {
        AccountCreateDto accountCreateDto = new AccountCreateDto("New Account", 5000, 1);

        when(clientRepository.findById(1)).thenReturn(Optional.of(client));

        Account savedAccount = new Account();
        savedAccount.setId(3);
        savedAccount.setName(accountCreateDto.getName());
        savedAccount.setBalance(accountCreateDto.getBalance());
        savedAccount.setClient(client);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Account result = accountService.createAccount(accountCreateDto);

        assertNotNull(result);
        assertEquals("New Account", result.getName());
        assertEquals(5000, result.getBalance());
        assertEquals(client, result.getClient());
    }

    @Test
    void updateAccount_Success() {
        AccountUpdateDto updateDto = new AccountUpdateDto("Updated Account", 20000);

        when(accountRepository.findById(1)).thenReturn(Optional.of(account1));
        when(accountRepository.save(any(Account.class))).thenReturn(account1);

        AccountGetDto result = accountService.updateAccount(1, updateDto);

        assertEquals("Updated Account", result.getName());
        assertEquals(20000, result.getBalance());
    }

    @Test
    void deleteAccount_Success() {
        when(accountRepository.findById(1)).thenReturn(Optional.of(account1));
        doNothing().when(accountRepository).delete(account1);

        assertDoesNotThrow(() -> accountService.deleteAccount(1));
        verify(accountRepository, times(1)).delete(account1);
    }

    @Test
    void deleteAccount_NotFound() {
        when(accountRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> accountService.deleteAccount(1));
    }
}

