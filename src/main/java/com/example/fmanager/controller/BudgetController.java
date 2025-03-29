package com.example.fmanager.controller;

import com.example.fmanager.dto.BudgetCreateDto;
import com.example.fmanager.dto.BudgetGetDto;
import com.example.fmanager.dto.BudgetUpdateDto;
import com.example.fmanager.models.Budget;
import com.example.fmanager.models.Category;
import com.example.fmanager.models.Client;
import com.example.fmanager.service.BudgetService;
import com.example.fmanager.service.CategoryService;
import com.example.fmanager.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/budgets")
@Tag(name = "Budget Manager", description = "APIs for managing budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final ClientService clientService;

    public BudgetController(BudgetService budgetService, CategoryService categoryService, ClientService clientService) {
        this.budgetService = budgetService;
        this.categoryService = categoryService;
        this.clientService = clientService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("budgetCreateDto", new BudgetCreateDto(0, 0.0, 0, null));
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        List<Client> clients = clientService.findAllClients();
        model.addAttribute("clients", clients);

        return "budgets/create";
    }

    @PostMapping
    @Operation(summary = "Create a new budget")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "404", description = "Budget not found after creation")
    })
    public String createBudget(
            @Valid @ModelAttribute("budgetCreateDto") BudgetCreateDto budgetCreateDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            List<Client> clients = clientService.findAllClients();
            model.addAttribute("clients", clients);
            return "budgets/create";
        }

        budgetService.createBudget(budgetCreateDto);
        return "redirect:/budgets";
    }

    @GetMapping
    @Operation(summary = "Get all budgets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of budgets retrieved successfully")
    })
    public String getAllBudgets(Model model) {
        List<BudgetGetDto> budgets = budgetService.getAllBudgets();
        model.addAttribute("budgets", budgets);
        return "budgets/list";
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget found"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    public String getBudget(
            @Parameter(description = "ID of the budget to retrieve", example = "1")
            @PathVariable int id,
            Model model
    ) {
        return budgetService.getBudgetById(id)
                .map(budget -> {
                    model.addAttribute("budget", budget);
                    List<Client> clients = clientService.findAllClients();
                    model.addAttribute("clients", clients);
                    return "budgets/details";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found"));
    }

    @GetMapping("/delete/{id}")
    @Operation(summary = "Delete Budget by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Budget deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found")
    })
    public String deleteBudget(
            @Parameter(description = "ID of the budget to update", example = "1")
            @PathVariable int id
    ) {
        budgetService.deleteBudget(id);
        return "redirect:/budgets";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable int id, Model model) {
        return budgetService.getBudgetById(id)
                .map(budget -> {
                    model.addAttribute("budget", budget);
                    List<Client> clients = clientService.findAllClients();
                    model.addAttribute("clients", clients);
                    BudgetUpdateDto budgetUpdateDto = new BudgetUpdateDto(budget.getPeriod(), budget.getLimitation(), budget.getClientIds());
                    model.addAttribute("budgetUpdateDto", budgetUpdateDto);
                    return "budgets/edit";
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found"));
    }

    @PostMapping("/edit/{id}")
    @Operation(summary = "Update budget by ID", description = "Updates an existing budget")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Budget updated successfully"),
            @ApiResponse(responseCode = "404", description = "Budget not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public String updateBudget(
            @Parameter(description = "ID of the budget to update", example = "1")
            @PathVariable int id,
            @Valid @ModelAttribute("budgetUpdateDto") BudgetUpdateDto budgetUpdateDto,
            BindingResult bindingResult,
            Model model
    ) {
        BudgetGetDto budget = budgetService.getBudgetById(id).orElse(null);
        if (bindingResult.hasErrors() || budget == null) {
            List<Client> clients = clientService.findAllClients();
            model.addAttribute("clients", clients);
            model.addAttribute("budget", budget);
            return "budgets/edit";
        }

        budgetService.updateBudget(id, budgetUpdateDto);
        return "redirect:/budgets/" + id;
    }
}