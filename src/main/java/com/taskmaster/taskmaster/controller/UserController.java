package com.taskmaster.taskmaster.controller;

import com.taskmaster.taskmaster.dto.UpdateProfileRequest;
import com.taskmaster.taskmaster.dto.UserResponse;
import com.taskmaster.taskmaster.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // The JWT filter stores the logged-in user's email as the authentication name.
    @GetMapping("/me")
    public UserResponse getMyProfile(Authentication authentication) {
        return userService.getProfile(authentication.getName());
    }

    @PutMapping("/me")
    public UserResponse updateMyProfile(Authentication authentication,
                                        @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(authentication.getName(), request);
    }
}
