package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.ACCOUNT_NOT_FOUND_MESSAGE;

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
    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
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

    public Accounts createAccount(Accounts account) {
        return accountRepository.save(account);
    }

    @Transactional
    public AccountDto updateAccount(int id, Accounts accountDetails) {
        Accounts account = accountRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(ACCOUNT_NOT_FOUND_MESSAGE));
        account.setName(accountDetails.getName());
        account.setBalance(accountDetails.getBalance());
        account.setClient(accountDetails.getClient());
        account.setTransactions(accountDetails.getTransactions());
        return AccountDto.convertToDto(accountRepository.save(account));
    }

    @Transactional
    public void deleteAccount(int id) {
        Accounts account = accountRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(ACCOUNT_NOT_FOUND_MESSAGE));
        accountRepository.delete(account);
    }
}
