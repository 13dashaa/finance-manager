package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.CLIENT_NOT_FOUND_MESSAGE;
import static com.example.fmanager.exception.NotFoundMessages.GOAL_NOT_FOUND_MESSAGE;

import com.example.fmanager.dto.GoalCreateDto;
import com.example.fmanager.dto.GoalGetDto;
import com.example.fmanager.exception.NotFoundException;
import com.example.fmanager.models.Client;
import com.example.fmanager.models.Goal;
import com.example.fmanager.repository.ClientRepository;
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
    private final ClientRepository clientRepository;

    public GoalService(GoalRepository goalRepository,
                       InMemoryCache cache,
                       ClientRepository clientRepository) {
        this.goalRepository = goalRepository;
        this.cache = cache;
        this.clientRepository = clientRepository;
    }

    public Optional<GoalGetDto> getGoalById(int id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(GOAL_NOT_FOUND_MESSAGE));
        return Optional.of(GoalGetDto.convertToDto(goal));
    }

    public List<GoalGetDto> getAllGoals() {
        List<Goal> goals = goalRepository.findAll();
        List<GoalGetDto> goalsDtos = new ArrayList<>();
        for (Goal goal : goals) {
            goalsDtos.add(GoalGetDto.convertToDto(goal));
        }
        return goalsDtos;
    }

    public List<GoalGetDto> findByClientId(int clientId) {
        String cacheKey = "goals_client_" + clientId;
        if (cache.containsKey(cacheKey)) {
            return (List<GoalGetDto>) cache.get(cacheKey);
        }
        List<Goal> goals = goalRepository.findByClientId(clientId);
        List<GoalGetDto> goalsDtos = new ArrayList<>();
        for (Goal goal : goals) {
            goalsDtos.add(GoalGetDto.convertToDto(goal));
        }
        cache.put(cacheKey, goalsDtos);
        return goalsDtos;
    }

    public void clearCacheForClient(int clientId) {
        String cacheKey = "goals_client_" + clientId;
        cache.remove(cacheKey);
    }

    public Goal createGoal(GoalCreateDto goalCreateDto) {
        Client client = clientRepository.findById(goalCreateDto.getClientId())
                .orElseThrow(() -> new RuntimeException(CLIENT_NOT_FOUND_MESSAGE));
        Goal goal = new Goal();
        goal.setName(goalCreateDto.getName());
        goal.setStartDate(goalCreateDto.getStartDate());
        goal.setEndDate(goalCreateDto.getEndDate());
        goal.setTargetAmount(goalCreateDto.getTargetAmount());
        goal.setClient(client);
        Goal savedGoal = goalRepository.save(goal);
        clearCacheForClient(savedGoal.getClient().getId());
        return savedGoal;
    }

    @Transactional
    public GoalGetDto updateGoal(int id, Goal goalDetails) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(GOAL_NOT_FOUND_MESSAGE));
        if (goal.getClient().getId() != goalDetails.getClient().getId()) {
            clearCacheForClient(goal.getClient().getId());
        }
        goal.setName(goalDetails.getName());
        goal.setTargetAmount(goalDetails.getTargetAmount());
        goal.setCurrentAmount(goalDetails.getCurrentAmount());
        goal.setClient(goalDetails.getClient());
        goal.setEndDate(goalDetails.getEndDate());
        goal.setStartDate(goalDetails.getStartDate());
        Goal savedGoal = goalRepository.save(goal);
        clearCacheForClient(savedGoal.getClient().getId());
        return GoalGetDto.convertToDto(savedGoal);
    }

    @Transactional
    public void deleteGoal(int id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(GOAL_NOT_FOUND_MESSAGE));
        clearCacheForClient(goal.getClient().getId());
        goalRepository.delete(goal);
    }
}
