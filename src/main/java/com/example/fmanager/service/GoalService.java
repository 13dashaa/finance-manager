package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.GOAL_NOT_FOUND_MESSAGE;

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
    private final GoalRepository goalRepository;
    private final InMemoryCache cache;

    public GoalService(GoalRepository goalRepository, InMemoryCache cache) {
        this.goalRepository = goalRepository;
        this.cache = cache;
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

    public List<GoalDto> findByClientId(int clientId) {
        String cacheKey = "goals_client_" + clientId;
        if (cache.containsKey(cacheKey)) {
            return (List<GoalDto>) cache.get(cacheKey);
        }
        List<Goals> goals = goalRepository.findByClientId(clientId);
        List<GoalDto> goalsDtos = new ArrayList<>();
        for (Goals goal : goals) {
            goalsDtos.add(GoalDto.convertToDto(goal));
        }
        cache.put(cacheKey, goalsDtos);
        return goalsDtos;
    }

    public void clearCacheForClient(int clientId) {
        String cacheKey = "goals_client_" + clientId;
        cache.remove(cacheKey);
    }

    public Goals createGoal(Goals goal) {
        Goals savedGoal = goalRepository.save(goal);
        clearCacheForClient(savedGoal.getClient().getId());
        return savedGoal;
    }

    @Transactional
    public GoalDto updateGoal(int id, Goals goalDetails) {
        Goals goal = goalRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(GOAL_NOT_FOUND_MESSAGE));
        if (goal.getClient().getId() != goalDetails.getClient().getId()) {
            clearCacheForClient(goal.getClient().getId());
        }
        goal.setName(goalDetails.getName());
        goal.setTargetAmount(goalDetails.getTargetAmount());
        goal.setCurrentAmount(goalDetails.getCurrentAmount());
        goal.setClient(goalDetails.getClient());
        goal.setEndDate(goalDetails.getEndDate());
        goal.setStartDate(goalDetails.getStartDate());
        Goals savedGoal = goalRepository.save(goal);
        clearCacheForClient(savedGoal.getClient().getId());
        return GoalDto.convertToDto(savedGoal);
    }

    @Transactional
    public void deleteGoal(int id) {
        Goals goal = goalRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(GOAL_NOT_FOUND_MESSAGE));
        clearCacheForClient(goal.getClient().getId());
        goalRepository.delete(goal);
    }
}
