package com.marketplace.backend.controller;


import com.marketplace.backend.dto.RegisterRequest;
import com.marketplace.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
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
    public String registerUser(@RequestBody RegisterRequest request){
        return  userService.registerUser(request);
    }


}
