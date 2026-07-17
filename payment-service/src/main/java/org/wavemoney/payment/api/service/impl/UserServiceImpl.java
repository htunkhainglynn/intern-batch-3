package org.wavemoney.payment.api.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.request.UserRequest;
import org.wavemoney.payment.api.dto.response.UserResponse;
import org.wavemoney.payment.api.entity.User;
import org.wavemoney.payment.api.repository.UserRepository;
import org.wavemoney.payment.api.service.UserService;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public UserResponse createUser(UserRequest userRequest) {
        User user = mapToUser(userRequest);
        user = userRepository.save(user);
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder().name(user.getName()).phone(user.getPhone()).level(user.getLevel()).build();
    }

    private User mapToUser(UserRequest userRequest) {
        return User.builder().name(userRequest.name()).phone(userRequest.phone()).nrc(userRequest.nrc()).pin(userRequest.pin()).build();
    }
}
