package org.wavemoney.payment.api.service;

import org.wavemoney.payment.api.dto.request.UserRequest;
import org.wavemoney.payment.api.dto.response.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest user);
    UserResponse getUserByPhone(String phone);
    UserResponse updateUserByPhone(String phone, UserRequest userRequest);
    void deleteUserByPhone(String phone);
    List<UserResponse> getAllUsers();
}