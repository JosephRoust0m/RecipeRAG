package com.ecse428.WaitForDinner.dto;

public class LoginResponseDto {
	private int userId;
	private String name;
	private String email;

	public LoginResponseDto(int userId, String name, String email) {
		this.userId = userId;
		this.name = name;
		this.email = email;
	}

	public int getUserId() {
		return this.userId;
	}

	public String getName() {
		return this.name;
	}

	public String getUsername() {
		return this.name;
	}

	public String getEmail() {
		return this.email;
	}
}
