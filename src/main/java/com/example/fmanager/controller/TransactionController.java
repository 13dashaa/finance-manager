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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/transactions")
@Tag(name = "Transaction Management", description = "APIs for managing transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final AccountService accountService;

    public TransactionController(TransactionService transactionService, CategoryService categoryService, AccountService accountService) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.accountService = accountService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("transactionCreateDto", new TransactionCreateDto("", 0.0, LocalDateTime.now(), 0, 0));
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
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
            @Valid @ModelAttribute("transactionCreateDto") TransactionCreateDto transactionCreateDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            List<Account> accounts = accountService.getAllAccounts();
            model.addAttribute("accounts", accounts);
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
                    return "transactions/details";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
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
        List<TransactionGetDto> transactions = transactionService.findByClientIdAndCategoryId(clientId, categoryId);
        model.addAttribute("transactions", transactions);
        return "transactions/list";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable int id, Model model) {
        return transactionService.getTransactionById(id)
                .map(transaction -> {
                    model.addAttribute("transaction", transaction);
                    TransactionCreateDto transactionCreateDto = new TransactionCreateDto(transaction.getDescription(), transaction.getAmount(), transaction.getDate(), transaction.getCategoryId(), transaction.getAccountId());
                    model.addAttribute("transactionCreateDto", transactionCreateDto);
                    List<Category> categories = categoryService.getAllCategories();
                    model.addAttribute("categories", categories);
                    List<Account> accounts = accountService.getAllAccounts();
                    model.addAttribute("accounts", accounts);
                    return "transactions/edit";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    @PostMapping("/edit/{id}")
    @Operation(summary = "Update transaction by ID",
            description = "Updates an existing transaction with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public String updateTransaction(
            @Parameter(description = "ID of the transaction to update", example = "1")
            @PathVariable int id,
            @Valid @ModelAttribute("transactionCreateDto") TransactionCreateDto transactionDetails,
            BindingResult bindingResult,
            Model model
    ) {
        TransactionGetDto transaction = transactionService.getTransactionById(id).orElse(null);
        if (bindingResult.hasErrors() || transaction == null) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            List<Account> accounts = accountService.getAllAccounts();
            model.addAttribute("accounts", accounts);
            model.addAttribute("transaction", transaction);
            return "transactions/edit";
        }
        transactionService.updateTransaction(id, transactionDetails);
        return "redirect:/transactions/" + id;
    }

    @GetMapping("/delete/{id}")
    @Operation(summary = "Delete transaction by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    public String deleteTransaction(
            @Parameter(description = "ID of the transaction to delete", example = "1")
            @PathVariable int id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }
}