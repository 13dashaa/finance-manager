package com.example.finance_manager.service;

import com.example.finance_manager.models.Transaction;

import com.example.finance_manager.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void addTransaction(Transaction transaction) {
        transactionRepository.addTransaction(transaction);
    }
    public List<Transaction> getAllTransaction() {
        return transactionRepository.getTransaction();
    }
    public Optional<Transaction> getTransactionById(int id) {
        return transactionRepository.findTransactionById(id);
    }
    public List<Transaction> filterTransactions(Integer userId, Integer categoryId, LocalDateTime date) {
        return transactionRepository.filterTransactions(userId, categoryId, date);
    }
}
