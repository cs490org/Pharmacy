package com.cs490.group4.dto.authentication;

import com.cs490.group4.security.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    private String firstName, lastName, email, password;
    private Role role;

}
