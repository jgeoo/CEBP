package com.stock.stock.services;

import com.stock.stock.models.User;
import com.stock.stock.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Fetch all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Fetch a user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Create a new user
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Update an existing user
    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setPassword(userDetails.getPassword());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    // Delete a user
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
    }

    public User login(String username, String password) {
        // Find user by username
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            // Check if the password matches
            if (user.get().getPassword().equals(password)) {
                return user.get(); // Return user if credentials are valid
            } else {
                throw new RuntimeException("Invalid password"); // Invalid password
            }
        } else {
            throw new RuntimeException("User not found"); // User does not exist
        }
    }
}
