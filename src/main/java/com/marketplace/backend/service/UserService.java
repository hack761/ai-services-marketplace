package com.marketplace.backend.service;

import com.marketplace.backend.dto.LoginRequest;
import com.marketplace.backend.dto.LoginResponse;
import com.marketplace.backend.dto.RegisterRequest;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.exception.EmailAlreadyExistsException;
import com.marketplace.backend.repository.UserRepository;
import com.marketplace.backend.security.JwtUtil;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository,
                       JwtUtil jwtUtil,
                       BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // REGISTER USER
    public String registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();

        user.setUsername(request.getName());
        user.setEmail(request.getEmail());

        // Encrypt Password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(request.getRole().toUpperCase());

        userRepository.save(user);

        return "Registration Successful";
    }

    // LOGIN USER
    public LoginResponse loginUser(LoginRequest request) {

        Optional<User> optionalUser =
                userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = optionalUser.get();

        // Compare entered password with DB password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid credentials");
        }

        // Check role
        if (!user.getRole().equals(request.getRole())) {
            throw new RuntimeException("Roles don't match");
        }

        // Generate JWT token
        String token =
                jwtUtil.generateToken(user.getEmail());

        // Send token in response
        return new LoginResponse(
                token,
                user.getRole()
        );
    }
}