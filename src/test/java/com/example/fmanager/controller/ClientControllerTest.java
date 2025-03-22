package com.example.fmanager.controller;

import com.example.fmanager.dto.*;
import com.example.fmanager.models.Client;
import com.example.fmanager.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private ClientCreateDto clientCreateDto;
    private ClientGetDto clientGetDto;
    private ClientUpdateDto clientUpdateDto;
    private Client client;

    @BeforeEach
    void setUp() {
        clientCreateDto = new ClientCreateDto("testuser", "password", "test@example.com");
        clientGetDto = new ClientGetDto();
        clientGetDto.setId(1);
        clientGetDto.setUsername("testuser");
        clientGetDto.setEmail("test@example.com");
        clientUpdateDto = new ClientUpdateDto("updateduser");
        client = new Client();
        client.setId(1);
        client.setUsername("testuser");
        client.setPassword("password");
        client.setEmail("test@example.com");
    }

    @Test
    void createUser_Success() {
        when(clientService.createUser(any(ClientCreateDto.class))).thenReturn(client);
        when(clientService.findById(1)).thenReturn(Optional.of(clientGetDto));

        ResponseEntity<ClientGetDto> response = clientController.createUser(clientCreateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    void createUser_NotFoundAfterCreation() {
        when(clientService.createUser(any(ClientCreateDto.class))).thenReturn(client);
        when(clientService.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<ClientGetDto> response = clientController.createUser(clientCreateDto);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getClients_ReturnsList() {
        when(clientService.findAll()).thenReturn(List.of(clientGetDto));

        List<ClientGetDto> result = clientController.getClients();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void getClientById_Found() {
        when(clientService.findById(1)).thenReturn(Optional.of(clientGetDto));

        ResponseEntity<ClientGetDto> response = clientController.getClientById(1);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    void getClientById_NotFound() {
        when(clientService.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<ClientGetDto> response = clientController.getClientById(1);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void deleteClient_Success() {
        doNothing().when(clientService).deleteUser(1);

        assertDoesNotThrow(() -> clientController.deleteClient(1));
        verify(clientService, times(1)).deleteUser(1);
    }

    @Test
    void createClientsBulk_Success() {
        BulkCreateDto<ClientCreateDto> bulk = new BulkCreateDto<>(List.of(clientCreateDto));

        when(clientService.createUser(any(ClientCreateDto.class))).thenReturn(client);
        when(clientService.findById(1)).thenReturn(Optional.of(clientGetDto));

        ResponseEntity<List<ClientGetDto>> response = clientController.createClientsBulk(bulk);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).getUsername());
    }

    @Test
    void updateClient_Success() {
        when(clientService.updateUser(eq(1), any(ClientUpdateDto.class))).thenReturn(clientGetDto);

        ResponseEntity<ClientGetDto> response = clientController.updateClient(1, clientUpdateDto);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        assertEquals("testuser", response.getBody().getUsername());
    }
}