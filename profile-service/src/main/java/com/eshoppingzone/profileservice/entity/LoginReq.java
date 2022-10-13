package com.eshoppingzone.profileservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class LoginReq {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

}
