package com.taskmaster.taskmaster.dto;

import com.taskmaster.taskmaster.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

// Never exposes the password hash.
@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
