package com.marketplace.backend.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Email(message = "Invalid Email Format")
    @NotBlank(message = "Email is Required")
    private String email;
    @NotBlank(message = "Password cant be Empty")
    @Size(min =5 , message = "Password must contain 5 characters")
    private String password;
    @NotBlank(message = "Enter Appropriate Role")
    private String role;


}
