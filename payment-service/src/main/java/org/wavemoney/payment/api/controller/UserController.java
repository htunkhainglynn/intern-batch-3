package org.wavemoney.payment.api.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wavemoney.payment.api.dto.request.UserRequest;
import org.wavemoney.payment.api.dto.response.ApiResponse;
import org.wavemoney.payment.api.dto.response.UserResponse;
import org.wavemoney.payment.api.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse created = userService.createUser(userRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, HttpStatus.CREATED.value(), "User created"));
    }

    @GetMapping("/{phone}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByPhone(@PathVariable String phone) {
        UserResponse user = userService.getUserByPhone(phone);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(user, "User fetched"));
    }

    @PutMapping("/{phone}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserByPhone(@PathVariable String phone, @Valid @RequestBody UserRequest userRequest) {
        UserResponse updated = userService.updateUserByPhone(phone, userRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updated, "User updated"));
    }

    @DeleteMapping("/{phone}")
    public ResponseEntity<ApiResponse<Void>> deleteUserByPhone(@PathVariable String phone) {
        userService.deleteUserByPhone(phone);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(null, "User deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(users, "All users fetched"));
    }
}