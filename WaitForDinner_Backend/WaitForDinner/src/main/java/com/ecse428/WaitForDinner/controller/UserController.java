package com.ecse428.WaitForDinner.controller;

import com.ecse428.WaitForDinner.dto.*;
import com.ecse428.WaitForDinner.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signup")
	public ResponseEntity<LoginResponseDto> signup(@RequestBody SignupRequestDto request, HttpSession session) {
		LoginResponseDto response = userService.signup(request, session);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request, HttpSession session) {
		return ResponseEntity.ok(userService.login(request, session));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpSession session) {
		userService.logout(session);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequestDto request) {
		userService.forgotPassword(request.getEmail());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/reset-password")
	public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequestDto request) {
		userService.resetPassword(request.getEmail(), request.getPassword());
		return ResponseEntity.noContent().build();
	}
	@DeleteMapping("/delete-account")
	public ResponseEntity<Void> deleteAccount(HttpSession session) {
		userService.deleteAccount(session);
		return ResponseEntity.noContent().build();
	}


}
