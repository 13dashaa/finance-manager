package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.GOAL_NOT_FOUND_MESSAGE;

import com.example.fmanager.dto.GoalDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Goal;
import com.example.fmanager.repository.GoalRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GoalService {
    private GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Optional<GoalDto> getGoalById(int id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(GOAL_NOT_FOUND_MESSAGE));
        return Optional.of(GoalDto.convertToDto(goal));
    }

    public List<GoalDto> getAllGoals() {
        List<Goal> goals = goalRepository.findAll();
        List<GoalDto> goalsDtos = new ArrayList<>();
        for (Goal goal : goals) {
            goalsDtos.add(GoalDto.convertToDto(goal));
        }
        return goalsDtos;
    }

    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    @Transactional
    public GoalDto updateGoal(int id, Goal goalDetails) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(GOAL_NOT_FOUND_MESSAGE));
        goal.setName(goalDetails.getName());
        goal.setTargetAmount(goalDetails.getTargetAmount());
        goal.setCurrentAmount(goalDetails.getCurrentAmount());
        goal.setClient(goalDetails.getClient());
        goal.setEndDate(goalDetails.getEndDate());
        goal.setStartDate(goalDetails.getStartDate());
        return GoalDto.convertToDto(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(int id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(GOAL_NOT_FOUND_MESSAGE));
        goalRepository.delete(goal);
    }
}
