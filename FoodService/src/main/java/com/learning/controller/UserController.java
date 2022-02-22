package com.learning.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.learning.dto.Address;
import com.learning.dto.FoodItem;
import com.learning.dto.Role;
import com.learning.dto.User;
import com.learning.enums.RoleName;
import com.learning.exception.IdNotFoundException;
import com.learning.exception.NoDataFoundException;
import com.learning.payload.request.LoginRequest;
import com.learning.payload.request.SignUpRequest;
import com.learning.payload.response.UserResponse;
import com.learning.repo.FoodItemRepo;
import com.learning.repo.RoleRepo;
import com.learning.service.UserService;
import com.learning.utils.Builder;

@RestController
public class UserController {
	Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	UserService userService;

	@Autowired
	FoodItemRepo foodItemRepo;
	@Autowired
	RoleRepo roleRepo;

	@PostMapping("/user/register")
	public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest request) {
		User user = Builder.buildUserFromSignUpRequest(request, roleRepo);
		return ResponseEntity.status(201).body(userService.addUser(user));
	}

	@PostMapping("/authenticate")
	public boolean authenticate(@Valid @RequestBody LoginRequest loginRequest) {
		if (userService.existsByEmail(loginRequest.getEmail())) {
			User user = userService.findByEmail(loginRequest.getEmail()).get();
			return user.getPassword().equals(loginRequest.getPassword());
		} else {
			return false;
		}
	}

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		List<User> list = userService.getAllUsers();
		List<UserResponse> response = new ArrayList<UserResponse>();
		list.forEach(e -> {
			response.add(Builder.buildUserResponse(e));
		});
		if (response.size() > 0) {
			return ResponseEntity.ok(response);
		} else {
			throw new NoDataFoundException("No records found");
		}
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<?> getUserById(@PathVariable("userId") Long userId) {
		User user = userService.getUserById(userId).orElseThrow(() -> new NoDataFoundException("User Not Found"));
		UserResponse response = Builder.buildUserResponse(user);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/users/{userId}")
	public ResponseEntity<?> updatePerson(@Valid @RequestBody User user, @PathVariable Long userId) {
		Map<String, Object> map = new HashMap<>();

		if (userService.existsById(userId)) {
			User existing = userService.getUserById(userId).get();
			existing.setAddresses(user.getAddresses());
			existing.setEmail(user.getEmail());
			existing.setName(user.getName());
			existing.setPassword(user.getPassword());
			userService.addUser(existing);
			map.put("message", "success");
			map.put("data", existing);
		} else {
			throw new NoDataFoundException("User not found");
		}
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(map);
	}

	@DeleteMapping("/users/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable long userId) {
		if (userService.existsById(userId)) {
			userService.deleteUserById(userId);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		} else {
			throw new NoDataFoundException("User not found, unable to delete");
		}
	}

//	@PostMapping("/food")
//	public ResponseEntity<?> saveFood(@Valid @RequestBody FoodItem food) {
//		try {
//			foodItemRepo.save(food);
//			Map<String, Object> map = new HashMap<>();
//			map.put("message", "success");
//			map.put("data", food);
//			return ResponseEntity.status(HttpStatus.CREATED).body((map));
//		} catch (Exception e) {
//			e.printStackTrace();
//			Map<String, Object> map = new HashMap<>();
//			map.put("message", "Add failed");
//			map.put("data", food);
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
//		}
//	}

//	@GetMapping("/food/{foodId}")
//	public Optional<FoodItem> getFoodById(@PathVariable Long foodId) {
//		return foodItemRepo.findById(foodId);
//	}
//
//	@GetMapping("/food")
//	public List<FoodItem> getAllFood() {
//		return foodItemRepo.findAll();
//	}
//
//	@DeleteMapping("/food/{foodId}")
//	public ResponseEntity<?> deleteFood(@PathVariable Long foodId) {
//		foodItemRepo.deleteById(foodId);
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("message", "success");
//		map.put("data", Long.toUnsignedString(foodId));
//		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(map);
//	}

}
