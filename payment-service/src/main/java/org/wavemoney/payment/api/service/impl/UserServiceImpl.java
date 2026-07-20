package org.wavemoney.payment.api.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.request.UserRequest;
import org.wavemoney.payment.api.dto.response.UserResponse;
import org.wavemoney.payment.api.entity.User;
import org.wavemoney.payment.api.exception.BusinessLogicException;
import org.wavemoney.payment.api.repository.UserRepository;
import org.wavemoney.payment.api.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        // Use business validation exception for duplicate entries
        userRepository.findByPhone(userRequest.phone()).ifPresent(u -> {
            throw BusinessLogicException.business("USER_ALREADY_EXISTS", "User with this phone number already exists.");
        });

        User user = User.builder()
                .name(userRequest.name())
                .phone(userRequest.phone())
                .nrc(userRequest.nrc())
                .pin(userRequest.pin())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> BusinessLogicException.notFound("USER_NOT_FOUND", "User not found with phone: " + phone));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateUserByPhone(String phone, UserRequest userRequest) {
        // Repaired code throwing your structural BusinessLogicException instead of RuntimeException
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> BusinessLogicException.notFound("USER_NOT_FOUND", "User not found with phone: " + phone));

        // Update properties
        user.setName(userRequest.name());
        user.setPhone(userRequest.phone());
        user.setNrc(userRequest.nrc());
        user.setPin(userRequest.pin());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Override
    public void deleteUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> BusinessLogicException.notFound("USER_NOT_FOUND", "User not found with phone: " + phone));
        userRepository.delete(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .level(user.getLevel())
                .walletStatus("ACTIVE")
                .build();
    }
}