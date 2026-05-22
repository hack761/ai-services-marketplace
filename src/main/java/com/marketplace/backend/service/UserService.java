package com.marketplace.backend.service;


import com.marketplace.backend.dto.RegisterRequest;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.exception.EmailAlreadyExistsException;
import com.marketplace.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private BCryptPasswordEncoder PasswordEncoder;
    private UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String registerUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(PasswordEncoder.encode(request.getPassword()));

        user.setRole("CUSTOMER");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        userRepository.save(user);
        return "Registration Successful";


    }
}
