package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.TRANSACTION_NOT_FOUND_MESSAGE;

import com.example.fmanager.dto.TransactionDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Transaction;
import com.example.fmanager.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        List<TransactionDto> transactionDtos = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionDtos.add(TransactionDto.convertToDto(transaction));
        }
        return transactionDtos;
    }

    public Optional<TransactionDto> getTransactionById(int id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(TRANSACTION_NOT_FOUND_MESSAGE));
        return Optional.of(TransactionDto.convertToDto(transaction));
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
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
        return TransactionDto.convertToDto(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(int id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(TRANSACTION_NOT_FOUND_MESSAGE));
        transactionRepository.delete(transaction);
    }
}
