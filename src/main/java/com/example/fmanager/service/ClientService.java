package com.example.fmanager.service;

import static com.example.fmanager.exception.NotFoundMessages.CLIENT_NOT_FOUND_MESSAGE;

import com.example.fmanager.dto.ClientDto;
import com.example.fmanager.exception.ExceptionNotFound;
import com.example.fmanager.models.Client;
import com.example.fmanager.repository.ClientRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private ClientRepository clientRepository;

    public ClientService(ClientRepository userRepository) {
        this.clientRepository = userRepository;
    }

    public List<ClientDto> findAll() {
        List<Client> clients = clientRepository.findAll();
        List<ClientDto> clientDtos = new ArrayList<>();
        for (Client client : clients) {
            clientDtos.add(ClientDto.convertToDto(client));
        }
        return clientDtos;
    }

    public Optional<ClientDto> findById(int id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(CLIENT_NOT_FOUND_MESSAGE));
        return Optional.of(ClientDto.convertToDto(client));
    }

    public Client createUser(Client user) {
        return clientRepository.save(user);
    }

    @Transactional
    public ClientDto updateUser(int id, Client userDetails) {
        Client user = clientRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(CLIENT_NOT_FOUND_MESSAGE));
        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        user.setEmail(userDetails.getEmail());
        user.setBudgets(userDetails.getBudgets());
        user.setAccounts(userDetails.getAccounts());
        return ClientDto.convertToDto(clientRepository.save(user));
    }

    @Transactional
    public void deleteUser(int id) {
        Client user = clientRepository.findById(id)
                .orElseThrow(() -> new ExceptionNotFound(CLIENT_NOT_FOUND_MESSAGE));
        clientRepository.delete(user);
    }
}
