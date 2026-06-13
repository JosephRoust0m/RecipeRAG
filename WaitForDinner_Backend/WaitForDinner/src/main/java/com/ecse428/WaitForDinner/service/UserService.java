package com.ecse428.WaitForDinner.service;

import com.ecse428.WaitForDinner.dto.LoginRequestDto;
import com.ecse428.WaitForDinner.dto.LoginResponseDto;
import com.ecse428.WaitForDinner.dto.SignupRequestDto;
import com.ecse428.WaitForDinner.model.User;
import com.ecse428.WaitForDinner.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class UserService {

	public static final String SESSION_USER_ID = "userId";

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JavaMailSender mailSender;
	private final static String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JavaMailSender mailSender) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailSender = mailSender;
	}

	public LoginResponseDto signup(SignupRequestDto request, HttpSession session) {
		if (!request.getEmail().matches(emailRegex)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email format is invalid");
		}
		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
		}

		if (request.getName() == null || request.getName().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
		}

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
		}
		validatePassword(request.getPassword());

		User user = new User(
				request.getName(),
				request.getEmail(),
				passwordEncoder.encode(request.getPassword()),
				new Date(),
				null,
				0
		);

		userRepository.save(user);

		UsernamePasswordAuthenticationToken auth =
				new UsernamePasswordAuthenticationToken(user, null, List.of());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);

		session.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				context
		);

		session.setAttribute(SESSION_USER_ID, user.getUserId());

		return new LoginResponseDto(
				user.getUserId(),
				user.getName(),
				user.getEmail()
		);
	}

	public LoginResponseDto login(LoginRequestDto request, HttpSession session) {
		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
		}
		if (request.getPassword() == null || request.getPassword().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
		}

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		}

		UsernamePasswordAuthenticationToken auth =
				new UsernamePasswordAuthenticationToken(user, null, List.of());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);

		session.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				context
		);

		session.setAttribute(SESSION_USER_ID, user.getUserId());

		return new LoginResponseDto(user.getUserId(), user.getName(), user.getEmail());
	}


	public void logout(HttpSession session) {
		if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in");
		}
		session.invalidate();
	}


	public void forgotPassword(String email) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		Date token = new Date(System.currentTimeMillis());
		user.setToken(token);

		userRepository.save(user);

		String resetLink = "http://localhost:5173/reset-password";

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Reset your password");
		message.setText(
				"Click the link below to reset your password:\n\n" +
						resetLink +
						"\n\nThis link expires in 15 minutes."
		);

		mailSender.send(message);
	}



	public void resetPassword(String email, String password) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		Date token = user.getToken();
		if (token == null ) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token does not exist");
		}
		LocalDateTime tokenTime = token.toInstant() .atZone(ZoneId.systemDefault()) .toLocalDateTime();
		LocalDateTime expiresAt = tokenTime.plusMinutes(15);

		 if (expiresAt.isBefore(LocalDateTime.now())) {
			 throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired");
		 }
		if (passwordEncoder.matches(password, user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "New password cannot be the same as old password");
		}
		validatePassword(password);

		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);

	}

	public int requireLoggedInUserId(HttpSession session) {
		Integer userId = (Integer) session.getAttribute(SESSION_USER_ID);
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not logged in");
		}
		return userId;
	}

	public void deleteAccount(HttpSession session) {
		Integer userId = (Integer) session.getAttribute(SESSION_USER_ID);
		if (userId == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not logged in");
		}
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		userRepository.delete(user);
		session.invalidate();
	}
	private void validatePassword(String password) {
		if (password == null || password.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
		}

		if (password.length() < 8) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters long");
		}

		if (!password.matches(".*\\d.*")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must contain at least one number");
		}

		if (!password.matches(".*[A-Za-z].*")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must contain at least one letter");
		}
	}
}
