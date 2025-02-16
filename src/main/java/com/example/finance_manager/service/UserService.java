package com.example.finance_manager.service;

import com.example.finance_manager.models.User;
import com.example.finance_manager.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }
    public List<User> getAllUsers() {
        return userRepository.getUsers();
    }
    public Optional<User> getUserById(int id) {
        return userRepository.findUserById(id);
    }

}
