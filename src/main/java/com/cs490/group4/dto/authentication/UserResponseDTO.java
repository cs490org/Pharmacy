package com.cs490.group4.dto.authentication;

import com.cs490.group4.security.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer id;
    private String firstName, lastName, email, imgUri;
    private Role role;
}
