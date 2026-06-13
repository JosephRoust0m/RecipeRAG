package com.ecse428.WaitForDinner.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecse428.WaitForDinner.dto.ProfileUpdateRequestDto;
import com.ecse428.WaitForDinner.dto.ProfileUpdateResponseDto;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.UserRepository;

@Service
public class ProfileService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfileUpdateResponseDto UpdateProfile(ProfileUpdateRequestDto req, int userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String newEmail = req.getEmail();
        String newName = req.getName();

        user.setEmail(newEmail);
        user.setName(newName);

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        userRepository.save(user);

        ProfileUpdateResponseDto res = new ProfileUpdateResponseDto();
        res.setUserId(userId);
        res.setEmail(newEmail);
        res.setName(newName);

        return res;
    }
}
