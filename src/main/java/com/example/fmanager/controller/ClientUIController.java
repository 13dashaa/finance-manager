package com.example.fmanager.controller;

import com.example.fmanager.dto.ClientGetDto;
import com.example.fmanager.exception.NotFoundException;
import com.example.fmanager.service.ClientService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/ui/clients")
public class ClientUIController {

    private final ClientService clientService;

    public ClientUIController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ModelAndView getAllClients(Model model) {
        model.addAttribute("clients", clientService.findAll());
        return new  ModelAndView("clients/list");
    }

    @GetMapping("/{id}")
    public  ModelAndView getClientById(@PathVariable int id, Model model) {
        ClientGetDto client = clientService.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        model.addAttribute("client", client);
        return new  ModelAndView("details");
    }
}