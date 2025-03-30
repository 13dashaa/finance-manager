package com.example.fmanager.controller;

import com.example.fmanager.dto.TransactionCreateDto;
import com.example.fmanager.dto.TransactionGetDto;
import com.example.fmanager.models.Account;
import com.example.fmanager.models.Category;
import com.example.fmanager.service.AccountService;
import com.example.fmanager.service.CategoryService;
import com.example.fmanager.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/transactions")
@Tag(name = "Transaction Management", description = "APIs for managing transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private static final String CATEGORIES = "categories";
    private static final String ACCOUNTS = "accounts";

    public TransactionController(
            TransactionService transactionService,
            CategoryService categoryService,
            AccountService accountService
    ) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.accountService = accountService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("transactionCreateDto",
                new TransactionCreateDto("", 0.0, LocalDateTime.now(), 0, 0));
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute(CATEGORIES, categories);
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute(ACCOUNTS, accounts);
        return "transactions/create";
    }

    @PostMapping
    @Operation(summary = "Create a new transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Transaction not found after creation")
    })
    public String createTransaction(
            @Valid
            @ModelAttribute("transactionCreateDto") TransactionCreateDto transactionCreateDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute(CATEGORIES, categories);
            List<Account> accounts = accountService.getAllAccounts();
            model.addAttribute(ACCOUNTS, accounts);
            return "transactions/create";
        }
        transactionService.createTransaction(transactionCreateDto);
        return "redirect:/transactions";
    }

    @GetMapping
    @Operation(summary = "Get all transactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public String getTransactions(Model model) {
        List<TransactionGetDto> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transactions/list";
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction found"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public String getTransaction(
            @Parameter(description = "ID of the transaction to retrieve", example = "1")
            @PathVariable int id,
            Model model
    ) {
        return transactionService.getTransactionById(id)
                .map(transaction -> {
                    model.addAttribute("transaction", transaction);
                    List<Category> categories = categoryService.getAllCategories();
                    model.addAttribute(CATEGORIES, categories);
                    List<Account> accounts = accountService.getAllAccounts();
                    model.addAttribute(ACCOUNTS, accounts);

                    TransactionCreateDto transactionUpdateDto = new TransactionCreateDto(
                            transaction.getDescription(),
                            transaction.getAmount(),
                            transaction.getDate(),
                            transaction.getCategoryId(),
                            transaction.getAccountId()
                    );
                    model.addAttribute("transactionUpdateDto", transactionUpdateDto);
                    return "transactions/details";
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Transaction not found"
                ));
    }

    @GetMapping("/filter")
    @Operation(summary = "Get transactions by client and category",
            description = "Retrieves transactions associated with a specific client and category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid client ID or category ID")
    })
    public String getTransactionsByClientAndCategory(
            @Parameter(description = "ID of the client to filter transactions", example = "1")
            @RequestParam int clientId,
            @Parameter(description = "ID of the category to filter transactions", example = "1")
            @RequestParam int categoryId,
            Model model
    ) {
        List<TransactionGetDto> transactions = transactionService
                .findByClientIdAndCategoryId(clientId, categoryId);
        model.addAttribute("transactions", transactions);
        return "transactions/list";
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update transaction by ID (API)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Object> updateTransactionApi(
            @Parameter(description = "ID of the transaction to update", example = "1")
            @PathVariable int id,
            @Valid @RequestBody TransactionCreateDto transactionUpdateDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(
                    "Validation errors: " + bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST
            );
        }
        try {
            transactionService.updateTransaction(id, transactionUpdateDto);
            return ResponseEntity.ok().body("{}");
        } catch (ResponseStatusException rse) {
            if (rse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(
                        "Transaction not found with id: " + id, HttpStatus.NOT_FOUND
                );
            }
            if (rse.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ResponseEntity<>(rse.getReason(), HttpStatus.BAD_REQUEST);
            }
            throw rse;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Transaction by ID (API)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public ResponseEntity<Object> deleteTransactionApi(
            @Parameter(description = "ID of the transaction to delete", example = "1")
            @PathVariable int id
    ) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException rse) {
            if (rse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(
                        "Transaction not found with id: " + id, HttpStatus.NOT_FOUND
                );
            }
            throw rse;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}