package com.example.demo.Services;

import com.example.demo.Dtos.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(String id);
    UserDTO updateUser(String id, UserDTO userDTO);
    void deleteUser(String id);
}
