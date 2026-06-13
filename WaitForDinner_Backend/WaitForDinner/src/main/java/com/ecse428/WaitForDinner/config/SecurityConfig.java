package com.ecse428.WaitForDinner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/api/auth/signup",
					"/api/auth/login",
					"/api/auth/logout",
					"/api/auth/forgot-password",
					"/api/auth/reset-password"
				).permitAll()
				.requestMatchers(HttpMethod.GET,
					"/api/preference",
					"/api/preference/**",
					"/api/diet",
					"/api/diet/**",
					"/api/ingredient",
					"/api/ingredient/**",
					"/api/recipe/byName"
				).permitAll()
				.anyRequest().authenticated()
			)
			.sessionManagement(session -> session
				.maximumSessions(1)
			);
		return http.build();
	}
}
