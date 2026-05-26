package com.marketplace.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RegisterRequest {
    @NotBlank(message = "Name is Required")
    private String name;
    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email Format")
    private String email;
    @NotBlank(message = "Password can't be Empty")
    @Size(min = 5 , message = "Password must contain 5 characters ")
    private String password;
    @NotBlank(message = "Role is Required")
    private String role;
}