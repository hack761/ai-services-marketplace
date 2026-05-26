package com.marketplace.backend.controller;


import com.marketplace.backend.dto.LoginRequest;
import com.marketplace.backend.dto.LoginResponse;
import com.marketplace.backend.dto.RegisterRequest;
import com.marketplace.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String hello(){
        return "Hello World";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody RegisterRequest request){
        return  userService.registerUser(request);
    }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        return userService.loginUser(request);
    }


}
