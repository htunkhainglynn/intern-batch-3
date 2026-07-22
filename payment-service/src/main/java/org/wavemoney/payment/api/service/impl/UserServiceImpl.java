package org.wavemoney.payment.api.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.wavemoney.payment.api.dto.request.UserRequest;
import org.wavemoney.payment.api.dto.request.WalletRequest;
import org.wavemoney.payment.api.dto.response.UserResponse;
import org.wavemoney.payment.api.dto.response.WalletResponse;
import org.wavemoney.payment.api.entity.User;
import org.wavemoney.payment.api.entity.Wallet;
import org.wavemoney.payment.api.enums.Currency;
import org.wavemoney.payment.api.enums.WalletStatus;
import org.wavemoney.payment.api.exception.BusinessLogicException;
import org.wavemoney.payment.api.repository.UserRepository;
import org.wavemoney.payment.api.repository.WalletRepository;
import org.wavemoney.payment.api.service.UserService;
import org.wavemoney.payment.api.service.WalletService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        // Prevent duplicate user creation by phone
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

        // 2. Build the WalletRequest using the new user's phone number
        WalletRequest walletRequest = WalletRequest.builder()
                .phone(savedUser.getPhone())
                .balance(BigDecimal.ZERO) // Initial default balance
                .currency(Currency.MMK)   // Default currency (e.g., MMK)
                .build();

        // 3. Automatically create the wallet
        WalletResponse walletResponse = walletService.createWallet(walletRequest);

        return mapToUserResponse(savedUser, walletResponse.walletStatus());
    }

    @Override
    public UserResponse getUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> BusinessLogicException.notFound("USER_NOT_FOUND", "User not found with phone: " + phone));

        WalletStatus walletStatus = walletService.getWalletStatusByPhone(phone);
        return mapToUserResponse(user, walletStatus);
    }

    @Override
    public UserResponse updateUserByPhone(String phone, UserRequest userRequest) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> BusinessLogicException.notFound("USER_NOT_FOUND", "User not found with phone: " + phone));

        user.setName(userRequest.name());
        user.setPhone(userRequest.phone());
        user.setNrc(userRequest.nrc());
        user.setPin(userRequest.pin());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        WalletStatus walletStatus = walletService.getWalletStatusByPhone(phone);
        return mapToUserResponse(updatedUser, walletStatus);
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
                .map(user -> {
                    WalletStatus walletStatus;
                    try {
                        walletStatus = walletService.getWalletStatusByPhone(user.getPhone());
                    } catch (Exception e) {
                        walletStatus = null;
                    }
                    return mapToUserResponse(user, walletStatus);
                })
                .toList();
    }

    private UserResponse mapToUserResponse(User user, WalletStatus walletStatus) {
        return UserResponse.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .level(user.getLevel())
                .walletStatus(walletStatus)
                .build();
    }

    @Override
    public UserResponse upgradeUserLevel(String phone, String newLevel) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> BusinessLogicException.notFound("USER_NOT_FOUND", "User not found with phone: " + phone));

        // Optional business check: ensure valid transition
        if ("2".equals(newLevel) && !"1".equals(user.getLevel())) {
            throw BusinessLogicException.business("INVALID_UPGRADE", "Only Level 1 users can be upgraded to Level 2.");
        }

        user.setLevel(newLevel);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        // Fetch the wallet status to build the complete response
        WalletStatus walletStatus = walletService.getWalletStatusByPhone(phone);

        return mapToUserResponse(updatedUser, walletStatus);
    }
}