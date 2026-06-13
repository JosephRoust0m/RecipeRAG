package com.ecse428.WaitForDinner.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecse428.WaitForDinner.repository.UserRepository;

@Service
public class AccountService {
    private final UserRepository userRepository;

    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deleteAccount(int userID) {
        userRepository.findById(userID)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        userRepository.deleteById(userID);
        return;
    }
}
