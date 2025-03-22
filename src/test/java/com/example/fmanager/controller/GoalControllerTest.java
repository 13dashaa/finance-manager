package com.example.fmanager.controller;

import com.example.fmanager.dto.GoalCreateDto;
import com.example.fmanager.dto.GoalGetDto;
import com.example.fmanager.models.Goal;
import com.example.fmanager.models.Client;
import com.example.fmanager.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalControllerTest {

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private GoalCreateDto goalCreateDto;
    private GoalGetDto goalGetDto;
    private Goal goal;
    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1);
        client.setUsername("testuser");
        client.setPassword("password");
        client.setEmail("test@example.com");

        goalCreateDto = new GoalCreateDto(
                "Test Goal",
                new BigDecimal("1000.00"),
                LocalDate.of(2023, 10, 1),
                LocalDate.of(2023, 12, 31),
                1
        );

        goalGetDto = new GoalGetDto();
        goalGetDto.setId(1);
        goalGetDto.setName("Test Goal");
        goalGetDto.setTargetAmount(new BigDecimal("1000.00"));
        goalGetDto.setCurrentAmount(new BigDecimal("0.00"));
        goalGetDto.setStartDate(LocalDate.of(2023, 10, 1));
        goalGetDto.setEndDate(LocalDate.of(2023, 12, 31));
        goalGetDto.setClientId(1);

        goal = new Goal();
        goal.setId(1);
        goal.setName("Test Goal");
        goal.setTargetAmount(new BigDecimal("1000.00"));
        goal.setCurrentAmount(new BigDecimal("0.00"));
        goal.setStartDate(LocalDate.of(2023, 10, 1));
        goal.setEndDate(LocalDate.of(2023, 12, 31));
        goal.setClient(client);
    }

    @Test
    void createGoal_Success() {
        when(goalService.createGoal(any(GoalCreateDto.class))).thenReturn(goal);
        when(goalService.getGoalById(1)).thenReturn(Optional.of(goalGetDto));

        ResponseEntity<GoalGetDto> response = goalController.createGoal(goalCreateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Goal", response.getBody().getName());
        assertEquals(new BigDecimal("1000.00"), response.getBody().getTargetAmount());
        assertEquals(LocalDate.of(2023, 10, 1), response.getBody().getStartDate());
        assertEquals(LocalDate.of(2023, 12, 31), response.getBody().getEndDate());
    }

    @Test
    void createGoal_NotFoundAfterCreation() {
        when(goalService.createGoal(any(GoalCreateDto.class))).thenReturn(goal);
        when(goalService.getGoalById(1)).thenReturn(Optional.empty());

        ResponseEntity<GoalGetDto> response = goalController.createGoal(goalCreateDto);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getGoals_ReturnsList() {
        when(goalService.getAllGoals()).thenReturn(List.of(goalGetDto));

        List<GoalGetDto> result = goalController.getGoals();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Goal", result.get(0).getName());
        assertEquals(new BigDecimal("1000.00"), result.get(0).getTargetAmount());
    }

    @Test
    void getGoalById_Found() {
        when(goalService.getGoalById(1)).thenReturn(Optional.of(goalGetDto));

        ResponseEntity<GoalGetDto> response = goalController.getGoalById(1);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Goal", response.getBody().getName());
        assertEquals(new BigDecimal("1000.00"), response.getBody().getTargetAmount());
    }

    @Test
    void getGoalById_NotFound() {
        when(goalService.getGoalById(1)).thenReturn(Optional.empty());

        ResponseEntity<GoalGetDto> response = goalController.getGoalById(1);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getGoalsByClient_Success() {
        when(goalService.findByClientId(1)).thenReturn(List.of(goalGetDto));

        List<GoalGetDto> result = goalController.getGoalsByClient(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Goal", result.get(0).getName());
        assertEquals(new BigDecimal("1000.00"), result.get(0).getTargetAmount());
    }

    @Test
    void deleteGoal_Success() {
        doNothing().when(goalService).deleteGoal(1);

        assertDoesNotThrow(() -> goalController.deleteGoal(1));
        verify(goalService, times(1)).deleteGoal(1);
    }

    @Test
    void updateGoal_Success() {
        when(goalService.updateGoal(eq(1), any(GoalCreateDto.class))).thenReturn(goalGetDto);

        ResponseEntity<GoalGetDto> response = goalController.updateGoal(1, goalCreateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Test Goal", response.getBody().getName());
        assertEquals(new BigDecimal("1000.00"), response.getBody().getTargetAmount());
    }
}