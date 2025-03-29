package com.example.fmanager.controller;

import com.example.fmanager.dto.GoalCreateDto;
import com.example.fmanager.dto.GoalGetDto;
import com.example.fmanager.models.Client;
import com.example.fmanager.models.Goal;
import com.example.fmanager.service.ClientService;
import com.example.fmanager.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/goals")
@Tag(name = "Goal Management", description = "APIs for managing goals")
public class GoalController {

    private final GoalService goalService;
    private final ClientService clientService;

    public GoalController(GoalService goalService, ClientService clientService) {
        this.goalService = goalService;
        this.clientService = clientService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("goalCreateDto",
                new GoalCreateDto("", null, null, null, 0));
        List<Client> clients = clientService.findAllClients();
        model.addAttribute("clients", clients);
        return "goals/create";
    }

    @PostMapping
    @Operation(summary = "Create a new goal",
            description = "Creates a new goal with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goal created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Goal not found after creation")
    })
    public String createGoal(
            @Valid @ModelAttribute("goalCreateDto") GoalCreateDto goalCreateDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "goals/create";
        }
        goalService.createGoal(goalCreateDto);
        return "redirect:/goals";
    }

    @GetMapping
    @Operation(summary = "Get all goals", description = "Retrieves a list of all goals")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goals retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public String getGoals(Model model) {
        List<GoalGetDto> goals = goalService.getAllGoals();
        model.addAttribute("goals", goals);
        return "goals/list";
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goal by ID", description = "Retrieves a goal by its unique ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goal found"),
        @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    public String getGoalById(
            @Parameter(description = "ID of the goal to retrieve", example = "1")
            @PathVariable int id,
            Model model) {
        return goalService.getGoalById(id)
                .map(goal -> {
                    model.addAttribute("goal", goal);
                    GoalCreateDto goalUpdateDto = new GoalCreateDto(
                            goal.getName(),
                            goal.getTargetAmount(),
                            goal.getCurrentAmount(),
                            goal.getStartDate(),
                            goal.getEndDate(),
                            goal.getClientId()
                    );
                    model.addAttribute("goalUpdateDto", goalUpdateDto);
                    return "goals/details";
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Goal not found"
                ));
    }

    @GetMapping("/filter")
    @Operation(summary = "Get goals by client ID",
            description = "Retrieves goals associated with a specific client ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goals retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid client ID")
    })
    public String getGoalsByClient(
            @Parameter(description = "Client ID to filter goals", example = "1")
            @RequestParam int clientId,
            Model model) {
        List<GoalGetDto> goals = goalService.findByClientId(clientId);
        model.addAttribute("goals", goals);
        return "goals/list";
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Goal by ID (API)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Goal deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @ResponseBody
    public ResponseEntity<?> deleteGoalApi(
            @Parameter(description = "ID of the goal to delete", example = "1")
            @PathVariable int id
    ) {
        try {
            goalService.deleteGoal(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException rse) {
            if (rse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(
                        "Goal not found with id: " + id, HttpStatus.NOT_FOUND
                );
            }
            throw rse;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update goal by ID (API)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Goal updated successfully"),
        @ApiResponse(responseCode = "404", description = "Goal not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @ResponseBody // Важно
    public ResponseEntity<?> updateGoalApi(
            @Parameter(description = "ID of the goal to update", example = "1")
            @PathVariable int id,
            @Valid @RequestBody GoalCreateDto goalUpdateDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(
                    "Validation errors: " + bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST
            );
        }
        if (goalUpdateDto.getStartDate() != null && goalUpdateDto.getEndDate() != null
                && goalUpdateDto.getStartDate().isAfter(goalUpdateDto.getEndDate())) {
            return new ResponseEntity<>(
                    "Validation error: Start date cannot be after end date.", HttpStatus.BAD_REQUEST
            );
        }
        try {
            goalService.updateGoal(id, goalUpdateDto);
            return ResponseEntity.ok().body("{}");
        } catch (ResponseStatusException rse) {
            if (rse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>("Goal not found with id: " + id, HttpStatus.NOT_FOUND);
            }
            throw rse;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}