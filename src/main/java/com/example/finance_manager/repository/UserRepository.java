package com.example.finance_manager.repository;


import com.example.finance_manager.models.User;
import lombok.Getter;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
@Getter
public  class UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    public void addUser(User user) {
        if (users.containsKey(user.getId())) {
            throw new IllegalArgumentException("Budget with id " + user.getId() + " already exists.");
        }
        users.put(user.getId(),user);
    }
    public Optional<User> findUserById(Integer id)  {
        return Optional.ofNullable(users.get(id));
    }
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

}
