package com.example.fmanager.controller;

import com.example.fmanager.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/ui/accounts")
public class AccountUIController {

    private final AccountService accountService;

    public AccountUIController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ModelAndView  getAllAccounts(Model model) {

        model.addAttribute("accounts", accountService.getAllAccounts());
        return new ModelAndView("accounts/list"); // Шаблон list.html
    }

    @GetMapping("/{id}")
    public ModelAndView getAccountDetails(@PathVariable int id, Model model) {
        accountService.getAccountById(id).ifPresent(account -> {
            model.addAttribute("account", account);
            //model.addAttribute("client", account.getClient()); // OneToMany
            //model.addAttribute("roles", account.getRoles());   // ManyToMany
        });
        return new ModelAndView("accounts/details"); // Шаблон details.html
    }
}