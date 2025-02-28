package com.example.fmanager.controller;

import java.util.List;
import com.example.fmanager.dto.AccountDto;
import com.example.fmanager.models.Accounts;
import com.example.fmanager.service.AccountService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<AccountDto> createAccount(@RequestBody Accounts account) {
        accountService.createAccount(account);
        return accountService
                .getAccountById(account.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<AccountDto> getAccounts(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Float limit) {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable int id) {
        return accountService
                .getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public List<AccountDto> getAccountsByClient(@RequestParam int clientId) {
        return  accountService.findByClientId(clientId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable int id,
                                                    @RequestBody Accounts accountDetails) {
        AccountDto updatedAccount = accountService.updateAccount(id, accountDetails);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable int id) {

        accountService.deleteAccount(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
