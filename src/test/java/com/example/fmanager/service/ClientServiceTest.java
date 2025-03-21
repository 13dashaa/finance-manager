package com.example.fmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.fmanager.dto.ClientCreateDto;
import com.example.fmanager.dto.ClientGetDto;
import com.example.fmanager.dto.ClientUpdateDto;
import com.example.fmanager.exception.NotFoundException;
import com.example.fmanager.models.Client;
import com.example.fmanager.repository.ClientRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1);
        client.setUsername("testuser");
        client.setPassword("password");
        client.setEmail("test@example.com");
    }

    @Test
    void findAll_ShouldReturnClientList() {
        when(clientRepository.findAll()).thenReturn(Arrays.asList(client));
        List<ClientGetDto> result = clientService.findAll();
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void findById_ShouldReturnClient_WhenClientExists() {
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        Optional<ClientGetDto> result = clientService.findById(1);
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenClientDoesNotExist() {
        when(clientRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> clientService.findById(1));
    }

/*    @Test
    void createUser_ShouldSaveAndReturnClient() {
        ClientCreateDto dto = new ClientCreateDto("newuser", "newpass", "new@example.com");
        Client newClient = new Client();
        newClient.setUsername(dto.getUsername());
        newClient.setPassword(dto.getPassword());
        newClient.setEmail(dto.getEmail());
        when(clientRepository.save(any(Client.class))).thenReturn(newClient);
        Client result = clientService.createUser(dto);
        assertEquals("newuser", result.getUsername());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnClient() {
        ClientUpdateDto dto = new ClientUpdateDto("updatedUser");
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        ClientGetDto result = clientService.updateUser(1, dto);
        assertEquals("updatedUser", result.getUsername());
    }*/

    @Test
    void deleteUser_ShouldDeleteClient_WhenClientExists() {
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);
        assertDoesNotThrow(() -> clientService.deleteUser(1));
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void deleteUser_ShouldThrowNotFoundException_WhenClientDoesNotExist() {
        when(clientRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> clientService.deleteUser(1));
    }
}