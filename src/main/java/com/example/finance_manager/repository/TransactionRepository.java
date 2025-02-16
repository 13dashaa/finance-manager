package com.example.finance_manager.repository;

import com.example.finance_manager.models.Transaction;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.*;

import java.time.LocalDateTime;

@Repository
@Getter
public  class TransactionRepository {
    private final Map<Integer, Transaction> transactions = new HashMap<>();
    public void addTransaction(Transaction transaction) {
        if (transactions.containsKey(transaction.getId())) {
            throw new IllegalArgumentException("Budget with id " + transaction.getId() + " already exists.");
        }
        transactions.put(transaction.getId(),transaction);
    }
    public Optional<Transaction> findTransactionById(Integer id)  {
        return Optional.ofNullable(transactions.get(id));
    }
    public List<Transaction> getTransaction() {
        return new ArrayList<>(transactions.values());
    }
    public List<Transaction> filterTransactions(Integer userId, Integer categoryId, LocalDateTime date) {
        return transactions.values().stream()
                .filter(transaction -> (userId == null || transaction.getUserId() == userId))
                .filter(transaction -> (categoryId == null || transaction.getCategoryId() == categoryId))
                .filter(transaction -> (date == null || transaction.getDate().isEqual(date)))
                .toList();
    }
}
