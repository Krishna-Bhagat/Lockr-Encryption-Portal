package com.hades.hades_portal.service.impl;

import com.hades.hades_portal.model.User;
import com.hades.hades_portal.repository.UserRepository;
import com.hades.hades_portal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(User user, String rawPassword){
        user.setPassword(rawPassword);
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Override
    public boolean validatePassword(String username, String rawPassword) {
        User user = findByUsername(username);
        return user != null && user.checkPassword(rawPassword);
    }
}
