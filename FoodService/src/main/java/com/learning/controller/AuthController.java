package com.learning.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learning.dto.User;
import com.learning.payload.request.SignUpRequest;
import com.learning.repo.RoleRepo;
import com.learning.service.UserService;
import com.learning.utils.Builder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	UserService userService;
	@Autowired
	RoleRepo roleRepo;

	@PostMapping("/register")
	public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest request) {
		User user = Builder.buildUserFromSignUpRequest(request, roleRepo);
		return ResponseEntity.status(201).body(userService.addUser(user));
	}
	
//	@PostMapping("/authenticate")
//	public boolean authenticate(@Valid @RequestBody LoginRequest loginRequest) {
//		if (userService.existsByEmail(loginRequest.getEmail())) {
//			User user = userService.findByEmail(loginRequest.getEmail()).get();
//			return user.getPassword().equals(loginRequest.getPassword());
//		} else {
//			return false;
//		}
//	}
}
