package com.example.fmanager.controller;

import com.example.fmanager.dto.ClientCreateDto;
import com.example.fmanager.dto.ClientGetDto;
import com.example.fmanager.models.Client;
import com.example.fmanager.service.ClientService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientGetDto> createUser(@Valid @RequestBody ClientCreateDto clientCreateDto) {
        Client client = clientService.createUser(clientCreateDto);
        return clientService
                .findById(client.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ClientGetDto> getClients() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientGetDto> getClientById(@PathVariable int id) {
        return clientService
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable int id) {
        clientService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientGetDto> updateClient(@PathVariable int id,
                                                     @RequestBody Client clientDetails) {
        ClientGetDto updatedClient = clientService.updateUser(id, clientDetails);
        return ResponseEntity.ok(updatedClient);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
