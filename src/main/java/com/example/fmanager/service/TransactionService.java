package com.example.fmanager.service;

import com.example.fmanager.dto.TransactionDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Transactions;
import com.example.fmanager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    public static final String TRANSACTION_NOT_FOUND_MESSAGE = "Transaction not found";

    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionDto> getAllTransactions() {
        List<Transactions> transactions = transactionRepository.findAll();
        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transactions transaction : transactions) {
            transactionDtos.add(TransactionDto.convertToDto(transaction));
        }
        return transactionDtos;
    }

    public Optional<TransactionDto> getTransactionById(int id) {
        Transactions transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(TRANSACTION_NOT_FOUND_MESSAGE));
        return Optional.of(TransactionDto.convertToDto(transaction));
    }

    public Transactions createTransaction(Transactions transaction) {
        return transactionRepository.save(transaction);
    }

    @Transactional
    public TransactionDto updateTransaction(int id, Transactions transactionDetails) {
        Transactions transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(TRANSACTION_NOT_FOUND_MESSAGE));
        transaction.setDescription(transactionDetails.getDescription());
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setDate(transactionDetails.getDate());
        transaction.setAccount(transactionDetails.getAccount());
        transaction.setCategory(transactionDetails.getCategory());
        return TransactionDto.convertToDto(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(int id) {
        Transactions transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(TRANSACTION_NOT_FOUND_MESSAGE));
        transactionRepository.delete(transaction);
    }
}
