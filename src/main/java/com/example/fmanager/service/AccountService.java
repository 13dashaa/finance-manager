package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.ACCOUNT_NOT_FOUND_MESSAGE;

import com.example.fmanager.dto.AccountDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Account;
import com.example.fmanager.repository.AccountRepository;
import com.example.fmanager.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionService transactionService;
    private final InMemoryCache cache;

    public AccountService(AccountRepository accountRepository,
                          InMemoryCache cache,
                          CategoryRepository categoryRepository,
                          TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.transactionService = transactionService;
        this.cache = cache;
    }

    public Optional<AccountDto> getAccountById(int id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(ACCOUNT_NOT_FOUND_MESSAGE));
        return Optional.of(AccountDto.convertToDto(account));
    }

    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        List<AccountDto> accountDtos = new ArrayList<>();
        for (Account account : accounts) {
            accountDtos.add(AccountDto.convertToDto(account));
        }
        return accountDtos;
    }

    public List<AccountDto> findByClientId(int clientId) {
        String cacheKey = "accounts_client_" + clientId;
        if (cache.containsKey(cacheKey)) {
            return (List<AccountDto>) cache.get(cacheKey);
        }
        List<Account> accounts = accountRepository.findAllByClientId(clientId);
        List<AccountDto> accountDtos = new ArrayList<>();
        for (Account account : accounts) {
            accountDtos.add(AccountDto.convertToDto(account));
        }
        cache.put(cacheKey, accountDtos);
        return accountDtos;
    }

    public void clearCacheForClient(int clientId) {
        String cacheKey = "accounts_client_" + clientId;
        cache.remove(cacheKey);
    }

    public Account createAccount(Account account) {
        Account savedAccount = accountRepository.save(account);
        List<Integer> categoryIds = categoryRepository.findCategoryIdsByClientId(
                savedAccount.getClient().getId()
        );
        for (Integer categoryId : categoryIds) {
            transactionService.clearCacheForClientAndCategory(
                    savedAccount.getClient().getId(), categoryId
            );
        }
        clearCacheForClient(savedAccount.getClient().getId());
        return savedAccount;
    }

    @Transactional
    public AccountDto updateAccount(int id, Account accountDetails) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(ACCOUNT_NOT_FOUND_MESSAGE));
        account.setName(accountDetails.getName());
        account.setBalance(accountDetails.getBalance());
        Account savedAccount = accountRepository.save(account);
        clearCacheForClient(savedAccount.getClient().getId());
        return AccountDto.convertToDto(savedAccount);
    }

    @Transactional
    public void deleteAccount(int id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(ACCOUNT_NOT_FOUND_MESSAGE));
        clearCacheForClient(account.getClient().getId());
        accountRepository.delete(account);
    }
}
