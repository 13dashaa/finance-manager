package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.ACCOUNT_NOT_FOUND_MESSAGE;
import static com.example.fmanager.exception.NotFoundMessages.TRANSACTION_NOT_FOUND_MESSAGE;

import com.example.fmanager.dto.TransactionDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Account;
import com.example.fmanager.models.Transaction;
import com.example.fmanager.repository.AccountRepository;
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
    private final InMemoryCache cache;

    public TransactionService(TransactionRepository transactionsRepository,
                              AccountRepository accountRepository, InMemoryCache cache) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionsRepository;
        this.cache = cache;
    }

    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionDtos.add(TransactionDto.convertToDto(transaction));
        }
        return transactionDtos;
    }

    public List<TransactionDto> findByClientIdAndCategoryId(int clientId, int categoryId) {
        String cacheKey = "transactions_client_" + clientId + "_category_" + categoryId;
        if (cache.containsKey(cacheKey)) {
            return (List<TransactionDto>) cache.get(cacheKey);
        }
        List<Transaction> transactions = transactionRepository
                .findAllByClientIdAndCategoryId(clientId, categoryId);
        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionDtos.add(TransactionDto.convertToDto(transaction));
        }
        cache.put(cacheKey, transactionDtos);
        return transactionDtos;
    }

    public void clearCacheForClientAndCategory(int clientId, int categoryId) {
        String cacheKey = "transactions_client_" + clientId + "_category_" + categoryId;
        cache.remove(cacheKey);
    }

    public Optional<TransactionDto> getTransactionById(int id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(TRANSACTION_NOT_FOUND_MESSAGE));
        return Optional.of(TransactionDto.convertToDto(transaction));
    }

    public Transaction createTransaction(Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND_MESSAGE));
        clearCacheForClientAndCategory(account.getClient().getId(),
               savedTransaction.getCategory().getId());
        return savedTransaction;
    }

    @Transactional
    public TransactionDto updateTransaction(int id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(TRANSACTION_NOT_FOUND_MESSAGE));
        transaction.setDescription(transactionDetails.getDescription());
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setDate(transactionDetails.getDate());
        transaction.setAccount(transactionDetails.getAccount());
        transaction.setCategory(transactionDetails.getCategory());
        Transaction savedTransaction = transactionRepository.save(transaction);
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND_MESSAGE));
        clearCacheForClientAndCategory(account.getClient().getId(),
                savedTransaction.getCategory().getId());
        return TransactionDto.convertToDto(savedTransaction);
    }

    @Transactional
    public void deleteTransaction(int id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(TRANSACTION_NOT_FOUND_MESSAGE));
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND_MESSAGE));
        clearCacheForClientAndCategory(account.getClient().getId(),
                transaction.getCategory().getId());
        transactionRepository.delete(transaction);
    }
}
