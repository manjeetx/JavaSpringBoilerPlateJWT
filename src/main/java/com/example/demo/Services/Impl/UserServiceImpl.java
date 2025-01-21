package com.example.demo.Services.Impl;


import com.example.demo.Dtos.UserDTO;
import com.example.demo.Exceptions.UserNotFoundException;
import com.example.demo.Models.User;
import com.example.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.example.demo.Services.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = new User(
                null,
                userDTO.getName(),
                userDTO.getEmail(),
                userDTO.getAge(),
                userDTO.getPassword() // Store password but don't return it
        );
        User savedUser = userRepository.save(user);

        return UserDTO.builder()
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .age(savedUser.getAge())
                .id(savedUser.getId())
                .password(encoder.encode(savedUser.getPassword()))
                .build();
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserDTO.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .age(user.getAge())
                        .id(user.getId())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return UserDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .id(user.getId())
                .build();
    }

    @Override
    public UserDTO updateUser(String id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());

        // Only update password if a new one is provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(userDTO.getPassword());
        }

        User updatedUser = userRepository.save(user);


        return UserDTO.builder()
                .name(updatedUser.getName())
                .email(updatedUser.getEmail())
                .age(updatedUser.getAge())
                .id(updatedUser.getId())
                .build();
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }
}