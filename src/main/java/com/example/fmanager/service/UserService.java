package com.example.fmanager.service;

import com.example.fmanager.models.User;
import com.example.fmanager.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

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
