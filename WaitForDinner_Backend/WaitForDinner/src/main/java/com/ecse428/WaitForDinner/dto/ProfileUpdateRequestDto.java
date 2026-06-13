package com.ecse428.WaitForDinner.dto;

public class ProfileUpdateRequestDto {
    private String email;
    private String name;
    private String username;
    private String password;

    public ProfileUpdateRequestDto() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() {
        return name != null ? name : username;
    }

    public void setName(String name) { this.name = name; }

    public String getUsername() { return username != null ? username : name; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
