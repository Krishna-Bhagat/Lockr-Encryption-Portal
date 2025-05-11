package com.hades.hades_portal.service;

import com.hades.hades_portal.model.User;

public interface UserService {
    User registerUser(User user, String rawPassword);
    User findByUsername(String username);
    boolean validatePassword(String username, String rawPassword);
}