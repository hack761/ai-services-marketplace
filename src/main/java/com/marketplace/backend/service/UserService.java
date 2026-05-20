package com.marketplace.backend.service;


import com.marketplace.backend.dto.RegisterRequest;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public String registerUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        user.setRole("CUSTOMER");
        userRepository.save(user);
        return "Registration Successful";


    }
}
