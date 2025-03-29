package com.example.fmanager.controller;

import com.example.fmanager.dto.AccountCreateDto;
import com.example.fmanager.dto.AccountGetDto;
import com.example.fmanager.dto.AccountUpdateDto;
import com.example.fmanager.dto.BulkCreateDto;
import com.example.fmanager.models.Account;
import com.example.fmanager.models.Client;
import com.example.fmanager.service.AccountService;
import com.example.fmanager.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/accounts")
@Tag(name = "Account Management", description = "APIs for managing accounts")
public class AccountController {

    private final AccountService accountService;
    private final ClientService clientService;

    public AccountController(AccountService accountService, ClientService clientService) {
        this.accountService = accountService;
        this.clientService = clientService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("accountCreateDto", new AccountCreateDto("", 0.0, null));
        List<Client> clients = clientService.findAllClients();
        model.addAttribute("clients", clients);
        return "accounts/create";
    }

    @PostMapping
    @Operation(summary = "Create a new account",
            description = "Creates a new account with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public String createAccount(
            @Valid @ModelAttribute("accountCreateDto") AccountCreateDto accountCreateDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "accounts/create";
        }

        Account newAccount = accountService.createAccount(accountCreateDto);
        return "redirect:/accounts/" + newAccount.getId();
    }

    @GetMapping
    @Operation(summary = "Get all accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public String getAccounts(Model model) {
        List<AccountGetDto> accounts = accountService.findAll();
        model.addAttribute("accounts", accounts);
        return "accounts/list";
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieves an account by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public String getAccount(
            @Parameter(description = "ID of the account to retrieve", example = "1")
            @PathVariable int id,
            Model model
    ) {
        return accountService.getAccountById(id)
                .map(account -> {
                    model.addAttribute("account", account);
                    model.addAttribute("accountUpdateDto", new AccountUpdateDto(account.getName(), account.getBalance()));
                    return "accounts/details";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    @GetMapping("/filter")
    @Operation(summary = "Get accounts by client username",
            description = "Retrieves accounts associated with a specific client username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid client username")
    })
    public String getAccountsByClient(
            @Parameter(description = "Client username to filter accounts", example = "johndoe")
            @RequestParam String clientUsername,
            Model model
    ) {
        List<AccountGetDto> accounts = accountService.findByClientUsername(clientUsername);
        model.addAttribute("accounts", accounts);
        model.addAttribute("clientUsername", clientUsername);
        return "accounts/list";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable int id, Model model) {
        return accountService.getAccountById(id)
                .map(account -> {
                    model.addAttribute("account", account);
                    model.addAttribute("accountUpdateDto", new AccountUpdateDto(account.getName(), account.getBalance()));
                    return "accounts/edit";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }


    @PostMapping("/edit/{id}")
    @Operation(summary = "Update account by ID", description = "Updates an existing account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public String updateAccount(
            @Parameter(description = "ID of the account to update", example = "1")
            @PathVariable int id,
            @Valid @ModelAttribute("accountUpdateDto") AccountUpdateDto accountUpdateDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("account", accountService.getAccountById(id).orElse(null)); // Для отображения деталей
            return "accounts/edit";
        }

        accountService.updateAccount(id, accountUpdateDto);
        return "redirect:/accounts/" + id;
    }

    @GetMapping("/bulk")
    public String showBulkCreateForm(Model model) {
        model.addAttribute("bulkCreateDto", new BulkCreateDto<>());
        return "accounts/bulkCreate";
    }


    @PostMapping("/bulk")
    @Operation(summary = "Create multiple accounts",
            description = "Creates multiple accounts with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accounts created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public String createAccountsBulk(
            @Valid @ModelAttribute("bulkCreateDto") BulkCreateDto<AccountCreateDto> bulkCreateDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "accounts/bulkCreate";
        }

        bulkCreateDto.getItems().forEach(accountService::createAccount);
        return "redirect:/accounts";
    }

    @GetMapping("/delete/{id}")
    @Operation(summary = "Delete account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public String deleteAccount(
            @Parameter(description = "ID of the account to delete", example = "1")
            @PathVariable int id
    ) {
        accountService.deleteAccount(id);
        return "redirect:/accounts";
    }
}