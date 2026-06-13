package com.ecse428.WaitForDinner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecse428.WaitForDinner.service.AccountService;
import com.ecse428.WaitForDinner.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(HttpSession session) {
        int userID = userService.requireLoggedInUserId(session);
        accountService.deleteAccount(userID);
        return ResponseEntity.noContent().build();
    }
}
