package com.example.fmanager.service;

import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static com.example.fmanager.exception.NotFoundMessages.CLIENT_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.fmanager.dto.ClientCreateDto;
import com.example.fmanager.dto.ClientGetDto;
import com.example.fmanager.exception.NotFoundException;
import com.example.fmanager.models.Client;
import com.example.fmanager.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void testFindAll() {
        // Подготовка данных
        Client client1 = new Client();
        client1.setId(1);
        client1.setUsername("user1");
        client1.setEmail("user1@example.com");

        Client client2 = new Client();
        client2.setId(2);
        client2.setUsername("user2");
        client2.setEmail("user2@example.com");

        List<Client> clients = List.of(client1, client2);

        // Мокируем вызов repository.findAll
        when(clientRepository.findAll()).thenReturn(clients);

        // Вызов метода
        List<ClientGetDto> result = clientService.findAll();

        // Проверки
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user1@example.com", result.get(0).getEmail());

        assertEquals(2, result.get(1).getId());
        assertEquals("user2", result.get(1).getUsername());
        assertEquals("user2@example.com", result.get(1).getEmail());

        // Проверка, что метод findAll был вызван
        verify(clientRepository, times(1)).findAll();
    }
    @Test
    void testFindById() {
        // Подготовка данных
        Client client = new Client();
        client.setId(1);
        client.setUsername("user1");
        client.setEmail("user1@example.com");

        // Мокируем вызов repository.findById
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));

        // Вызов метода
        Optional<ClientGetDto> result = clientService.findById(1);

        // Проверки
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user1", result.get().getUsername());
        assertEquals("user1@example.com", result.get().getEmail());

        // Проверка, что метод findById был вызван
        verify(clientRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_NotFound() {
        when(clientRepository.findById(1)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            clientService.findById(1);
        });
        assertEquals(CLIENT_NOT_FOUND_MESSAGE, exception.getMessage());
        verify(clientRepository, times(1)).findById(1);
    }
    @Test
    void testCreateUser() {
        // Подготовка данных
        ClientCreateDto userCreateDto = new ClientCreateDto();
        userCreateDto.setUsername("user1");
        userCreateDto.setPassword("password1");
        userCreateDto.setEmail("user1@example.com");

        Client client = new Client();
        client.setId(1);
        client.setUsername("user1");
        client.setPassword("password1");
        client.setEmail("user1@example.com");

        // Мокируем вызов repository.save
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // Вызов метода
        Client result = clientService.createUser(userCreateDto);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("user1", result.getUsername());
        assertEquals("password1", result.getPassword());
        assertEquals("user1@example.com", result.getEmail());
        verify(clientRepository, times(1)).save(any(Client.class));
    }
    @Test
    void testUpdateUser() {
        // Подготовка данных
        Client existingClient = new Client();
        existingClient.setId(1);
        existingClient.setUsername("user1");
        existingClient.setPassword("password1");
        existingClient.setEmail("user1@example.com");

        Client updatedClientDetails = new Client();
        updatedClientDetails.setUsername("updatedUser");
        updatedClientDetails.setPassword("updatedPassword");
        updatedClientDetails.setEmail("updated@example.com");

        Client savedClient = new Client();
        savedClient.setId(1);
        savedClient.setUsername("updatedUser");
        savedClient.setPassword("updatedPassword");
        savedClient.setEmail("updated@example.com");

        // Мокируем вызов repository.findById и repository.save
        when(clientRepository.findById(1)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        // Вызов метода
        ClientGetDto result = clientService.updateUser(1, updatedClientDetails);

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("updatedUser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());

        // Проверка, что методы findById и save были вызваны
        verify(clientRepository, times(1)).findById(1);
        verify(clientRepository, times(1)).save(any(Client.class));
    }
    @Test
    void testDeleteUser() {
        // Подготовка данных
        Client client = new Client();
        client.setId(1);
        client.setUsername("user1");
        client.setEmail("user1@example.com");

        // Мокируем вызов repository.findById и repository.delete
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        doNothing().when(clientRepository).delete(client);

        // Вызов метода
        clientService.deleteUser(1);

        // Проверка, что методы findById и delete были вызваны
        verify(clientRepository, times(1)).findById(1);
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Мокируем вызов repository.findById для случая, когда клиент не найден
        when(clientRepository.findById(1)).thenReturn(Optional.empty());

        // Проверка, что выбрасывается исключение
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            clientService.deleteUser(1);
        });

        assertEquals(CLIENT_NOT_FOUND_MESSAGE, exception.getMessage());

        // Проверка, что метод findById был вызван
        verify(clientRepository, times(1)).findById(1);
        verify(clientRepository, never()).delete(any(Client.class));
    }
}