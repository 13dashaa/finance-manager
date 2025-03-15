package com.example.fmanager.controller;

import com.example.fmanager.dto.GoalCreateDto;
import com.example.fmanager.dto.GoalGetDto;
import com.example.fmanager.models.Goal;
import com.example.fmanager.service.GoalService;
import java.util.List;
import jakarta.validation.Valid;
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
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalGetDto> createCategory(@Valid @RequestBody GoalCreateDto goalCreateDto) {
        Goal goal = goalService.createGoal(goalCreateDto);
        return goalService
                .getGoalById(goal.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<GoalGetDto> getGoalss() {
        return goalService.getAllGoals();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalGetDto> getGoalById(@PathVariable int id) {
        return goalService
                .getGoalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filter")
    public List<GoalGetDto> getGoalsByClient(@RequestParam int clientId) {
        return  goalService.findByClientId(clientId);
    }

    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable int id) {
        goalService.deleteGoal(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalGetDto> updateGoal(@PathVariable int id,
                                                 @RequestBody Goal goalDetails) {
        GoalGetDto updatedGoal = goalService.updateGoal(id, goalDetails);
        return ResponseEntity.ok(updatedGoal);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
