package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.ACCOUNT_NOT_FOUND_MESSAGE;
import static com.example.fmanager.exception.NotFoundMessages.CATEGORY_NOT_FOUND_MESSAGE;
import static com.example.fmanager.exception.NotFoundMessages.TRANSACTION_NOT_FOUND_MESSAGE;

import com.example.fmanager.dto.TransactionCreateDto;
import com.example.fmanager.dto.TransactionGetDto;
import com.example.fmanager.exception.InvalidDataException;
import com.example.fmanager.exception.NotFoundException;
import com.example.fmanager.models.Account;
import com.example.fmanager.models.Category;
import com.example.fmanager.models.Transaction;
import com.example.fmanager.repository.AccountRepository;
import com.example.fmanager.repository.CategoryRepository;
import com.example.fmanager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final InMemoryCache cache;

    public TransactionService(TransactionRepository transactionsRepository,
                              AccountRepository accountRepository,
                              InMemoryCache cache,
                              CategoryRepository categoryRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionsRepository;
        this.categoryRepository = categoryRepository;
        this.cache = cache;
    }

    public List<TransactionGetDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        List<TransactionGetDto> transactionGetDtos = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionGetDtos.add(TransactionGetDto.convertToDto(transaction));
        }
        return transactionGetDtos;
    }

    public List<TransactionGetDto> findByClientIdAndCategoryId(int clientId, int categoryId) {
        String cacheKey = "transactions_client_" + clientId + "_category_" + categoryId;
        if (cache.containsKey(cacheKey)) {
            return (List<TransactionGetDto>) cache.get(cacheKey);
        }
        List<Transaction> transactions = transactionRepository
                .findAllByClientIdAndCategoryId(clientId, categoryId);
        List<TransactionGetDto> transactionGetDtos = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionGetDtos.add(TransactionGetDto.convertToDto(transaction));
        }
        cache.put(cacheKey, transactionGetDtos);
        return transactionGetDtos;
    }

    public void clearCacheForClientAndCategory(int clientId, int categoryId) {
        String cacheKey = "transactions_client_" + clientId + "_category_" + categoryId;
        cache.remove(cacheKey);
    }

    public Optional<TransactionGetDto> getTransactionById(int id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(TRANSACTION_NOT_FOUND_MESSAGE));
        return Optional.of(TransactionGetDto.convertToDto(transaction));
    }

    public Transaction createTransaction(TransactionCreateDto transactionCreateDto) {
        Transaction transaction = new Transaction();
        transaction.setDate(transactionCreateDto.getDate());
        transaction.setDescription(transactionCreateDto.getDescription());
        Account transactionalAccount = accountRepository
                .findById(transactionCreateDto.getAccountId())
                .orElseThrow(() -> new RuntimeException(ACCOUNT_NOT_FOUND_MESSAGE));
        if (transactionalAccount.getBalance() + transactionCreateDto.getAmount() < 0) {
            throw new InvalidDataException(
                    "Insufficient funds: transaction would result in negative balance"
            );
        }
        transaction.setAmount(transactionCreateDto.getAmount());
        transactionalAccount.setBalance(
                transactionalAccount.getBalance() + transaction.getAmount()
        );
        accountRepository.save(transactionalAccount);
        transaction.setAccount(transactionalAccount);
        Category category = categoryRepository.findById(transactionCreateDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException(CATEGORY_NOT_FOUND_MESSAGE));
        transaction.setCategory(category);
        Transaction savedTransaction = transactionRepository.save(transaction);
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND_MESSAGE));
        clearCacheForClientAndCategory(account.getClient().getId(),
               savedTransaction.getCategory().getId());
        return savedTransaction;
    }

    @Transactional
    public TransactionGetDto updateTransaction(int id, TransactionCreateDto transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(TRANSACTION_NOT_FOUND_MESSAGE));
        transaction.setDescription(transactionDetails.getDescription());
        transaction.setDate(transactionDetails.getDate());
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND_MESSAGE));
        double amountDifference = transactionDetails.getAmount() - transaction.getAmount();
        if (account.getBalance() + amountDifference < 0) {
            throw new InvalidDataException(
                    "Insufficient funds: transaction update would result in negative balance"
            );
        }
        transaction.setAmount(transactionDetails.getAmount());
        Transaction savedTransaction = transactionRepository.save(transaction);
        account.setBalance(account.getBalance() + amountDifference);
        accountRepository.save(account);
        clearCacheForClientAndCategory(account.getClient().getId(),
                savedTransaction.getCategory().getId());
        return TransactionGetDto.convertToDto(savedTransaction);
    }

    @Transactional
    public void deleteTransaction(int id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(TRANSACTION_NOT_FOUND_MESSAGE));
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND_MESSAGE));
        clearCacheForClientAndCategory(account.getClient().getId(),
                transaction.getCategory().getId());
        transactionRepository.delete(transaction);
    }
}
