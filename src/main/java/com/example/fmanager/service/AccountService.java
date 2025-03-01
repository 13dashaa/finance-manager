package com.example.fmanager.service;

import com.example.fmanager.dto.AccountDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Accounts;
import com.example.fmanager.repository.AccountRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    public static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found";
    private final AccountRepository accountRepository;
    private final InMemoryCache cache;

    public AccountService(AccountRepository accountRepository, InMemoryCache cache) {
        this.accountRepository = accountRepository;
        this.cache = cache;
    }

    public Optional<AccountDto> getAccountById(int id) {
        Accounts account = accountRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(ACCOUNT_NOT_FOUND_MESSAGE));
        return Optional.of(AccountDto.convertToDto(account));
    }

    public List<AccountDto> getAllAccounts() {
        List<Accounts> accounts = accountRepository.findAll();
        List<AccountDto> accountDtos = new ArrayList<>();
        for (Accounts account : accounts) {
            accountDtos.add(AccountDto.convertToDto(account));
        }
        return accountDtos;
    }

    public List<AccountDto> findByClientId(int clientId) {
        String cacheKey = "accounts_client_" + clientId;
        if (cache.containsKey(cacheKey)) {
            System.out.println("used cache accounts_client_");
            return (List<AccountDto>) cache.get(cacheKey);
        }
        List<Accounts> accounts = accountRepository.findAllByClientId(clientId);
        List<AccountDto> accountDtos = new ArrayList<>();
        for (Accounts account : accounts) {
            accountDtos.add(AccountDto.convertToDto(account));
        }
        cache.put(cacheKey, accountDtos);
        return accountDtos;
    }

    public void clearCacheForClient(int clientId) {
        String cacheKey = "accounts_client_" + clientId;
        cache.remove(cacheKey);
    }

    public Accounts createAccount(Accounts account) {
        Accounts savedAccount = accountRepository.save(account);
        clearCacheForClient(savedAccount.getClient().getId());
        return savedAccount;
    }

    @Transactional
    public AccountDto updateAccount(int id, Accounts accountDetails) {
        Accounts account = accountRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(ACCOUNT_NOT_FOUND_MESSAGE));
        account.setName(accountDetails.getName());
        account.setBalance(accountDetails.getBalance());
        /* account.setClient(accountDetails.getClient());
        account.setTransactions(accountDetails.getTransactions());*/
        Accounts savedAccount = accountRepository.save(account);
        clearCacheForClient(savedAccount.getClient().getId());
        return AccountDto.convertToDto(savedAccount);
    }

    @Transactional
    public void deleteAccount(int id) {
        Accounts account = accountRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(ACCOUNT_NOT_FOUND_MESSAGE));
        clearCacheForClient(account.getClient().getId());
        accountRepository.delete(account);
    }
}
