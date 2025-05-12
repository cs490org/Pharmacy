package com.cs490.group4.controller.authentication;

import com.cs490.group4.service.authentication.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@CookieValue("access_token") String accessToken) {
        return ResponseEntity.ok(userService.getUserDetails(accessToken));
    }

}
