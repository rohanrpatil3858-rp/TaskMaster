package com.taskmaster.taskmaster.service;

import com.taskmaster.taskmaster.dto.UpdateProfileRequest;
import com.taskmaster.taskmaster.dto.UserResponse;
import com.taskmaster.taskmaster.entity.User;
import com.taskmaster.taskmaster.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + email));
    }

    public UserResponse getProfile(String email) {
        return UserResponse.from(getUserByEmail(email));
    }

    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = getUserByEmail(email);
        user.setName(request.getName());
        return UserResponse.from(userRepository.save(user));
    }
}
