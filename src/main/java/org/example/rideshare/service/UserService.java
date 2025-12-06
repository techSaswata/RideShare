package org.example.rideshare.service;

import org.example.rideshare.exception.NotFoundException;
import org.example.rideshare.model.User;
import org.example.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public String getUserIdByUsername(String username) {
        User user = getUserByUsername(username);
        return user.getId();
    }

}


