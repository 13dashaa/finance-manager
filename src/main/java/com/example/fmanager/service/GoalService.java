package com.example.fmanager.service;

import com.example.fmanager.dto.GoalDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Goals;
import com.example.fmanager.repository.GoalRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class GoalService {
    public static final String GOAL_NOT_FOUND_MESSAGE = "Goal not found";
    private GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public Optional<GoalDto> getGoalById(int id) {
        Goals goal = goalRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(GOAL_NOT_FOUND_MESSAGE));
        return Optional.of(GoalDto.convertToDto(goal));
    }


    public List<GoalDto> getAllGoals() {
        List<Goals> goals = goalRepository.findAll();
        List<GoalDto> goalsDtos = new ArrayList<>();
        for (Goals goal : goals) {
            goalsDtos.add(GoalDto.convertToDto(goal));
        }
        return goalsDtos;
    }

    public Goals createGoal(Goals goal) {
        return goalRepository.save(goal);
    }

    @Transactional
    public GoalDto updateGoal(int id, Goals goalDetails) {
        Goals goal = goalRepository.findById(id)
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
        Goals goal = goalRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(GOAL_NOT_FOUND_MESSAGE));
        goalRepository.delete(goal);
    }
}
