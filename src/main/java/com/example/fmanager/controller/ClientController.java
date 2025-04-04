package com.example.fmanager.controller;

import com.example.fmanager.dto.BulkCreateDto;
import com.example.fmanager.dto.ClientCreateDto;
import com.example.fmanager.dto.ClientGetDto;
import com.example.fmanager.dto.ClientUpdateDto;
import com.example.fmanager.models.Client;
import com.example.fmanager.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/clients")
@Tag(name = "Client Management", description = "APIs for managing clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("clientCreateDto", new ClientCreateDto("", "", ""));
        return "clients/create";
    }

    @PostMapping
    @Operation(summary = "Create a new client",
            description = "Creates a new client with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Client not found after creation")
    })
    public String createUser(
            @Valid @ModelAttribute("clientCreateDto") ClientCreateDto clientCreateDto,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "clients/create";
        }

        Client client = clientService.createUser(clientCreateDto);
        return "redirect:/clients/" + client.getId();
    }

    @GetMapping
    @Operation(summary = "Get all clients", description = "Retrieves a list of all clients")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clients retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public String getClients(Model model) {
        List<ClientGetDto> clients = clientService.findAll();
        model.addAttribute("clients", clients);
        return "clients/list";
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID", description = "Retrieves a client by its unique ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client found"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public String getClientById(
            @Parameter(description = "ID of the client to retrieve", example = "1")
            @PathVariable int id,
            Model model) {
        return clientService.findById(id)
                .map(
                        client -> {
                            model.addAttribute("client", client);
                            model.addAttribute(
                                    "clientUpdateDto",
                                    new ClientUpdateDto(client.getUsername())
                            );
                            return "clients/details";
                        })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client not found"
                ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Client by ID (API)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<Object> deleteClientApi(
            @Parameter(description = "ID of the client to delete", example = "1")
            @PathVariable int id
    ) {
        try {
            clientService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException rse) {
            if (rse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(
                        "Client not found with id: " + id, HttpStatus.NOT_FOUND
                );
            }
            throw rse;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/bulk")
    public String showBulkCreateForm(@RequestParam(name = "count", defaultValue = "1") int count,
                                     Model model) {
        List<ClientCreateDto> clientCreateDtos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            clientCreateDtos.add(new ClientCreateDto("", "", ""));
        }
        BulkCreateDto<ClientCreateDto> bulkCreateDto = new BulkCreateDto<>(clientCreateDtos);
        model.addAttribute("bulkCreateDto", bulkCreateDto);
        model.addAttribute("count", count);
        return "clients/bulkCreate";
    }

    @PostMapping("/bulk")
    public String createClientsBulk(
            @Valid @ModelAttribute("bulkCreateDto") BulkCreateDto<ClientCreateDto> bulkCreateDto,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("bulkCreateDto", bulkCreateDto); // Add this line
            return "clients/bulkCreate";
        }

        bulkCreateDto.getItems().forEach(clientService::createUser);
        return "redirect:/clients";
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update client by ID (API)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client updated successfully"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Object> updateClientApi(
            @Parameter(description = "ID of the client to update", example = "1")
            @PathVariable int id,
            @Valid @RequestBody ClientUpdateDto clientUpdateDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(
                    "Validation errors: " + bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST
            );
        }
        try {
            clientService.updateUser(id, clientUpdateDto);
            return ResponseEntity.ok().body("{}");
        } catch (ResponseStatusException rse) {
            if (rse.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new ResponseEntity<>(
                        "Client not found with id: " + id, HttpStatus.NOT_FOUND
                );
            }
            throw rse;
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "An internal error occurred.", HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}