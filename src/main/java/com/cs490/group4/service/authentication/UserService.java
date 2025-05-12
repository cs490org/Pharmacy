package com.cs490.group4.service.authentication;

import com.cs490.group4.dto.authentication.UserResponseDTO;
import com.cs490.group4.security.User;
import com.cs490.group4.security.UserRepository;
import com.cs490.group4.security.config.token.PasswordResetToken;
import com.cs490.group4.security.config.token.Token;
import com.cs490.group4.security.config.token.TokenRepository;
import com.cs490.group4.security.config.token.TokenResetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenResetRepository tokenResetRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public UserResponseDTO getUserDetails(String accessToken) {
        Token token = tokenRepository.findByToken(accessToken).orElseThrow();

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(token.getUser().getUserId());
        userResponseDTO.setImgUri(token.getUser().getImgUri());
        userResponseDTO.setEmail(token.getUser().getEmail());
        userResponseDTO.setFirstName(token.getUser().getFirstName());
        userResponseDTO.setLastName(token.getUser().getLastName());
        userResponseDTO.setRole(token.getUser().getRole());

        return userResponseDTO;
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    public Integer getUserId(String accessToken) {
        Token token = tokenRepository.findByToken(accessToken).orElseThrow(EntityNotFoundException::new);

        return token.getUser().getUserId();
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void createPasswordResetToken(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken(token, user);

        /**
         * If a token already exists for this user, delete it before creating a new one
         * to avoid a key constraint violation.
         * There is no protection for many password reset attempts here.
         */
        tokenResetRepository.findByUserId(user.getUserId())
                .ifPresent(passwordResetToken -> tokenResetRepository.delete(passwordResetToken));

        tokenResetRepository.save(resetToken);
    }

    public void updateUserProfileImageUri(Integer userId, String imageUri) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userId);
        params.addValue("imageUri", imageUri);

        jdbcTemplate.update("update users set img_uri = :imageUri where user_id = :userId", params);
    }

    public boolean isValidResetToken(String email, String token) {
        Integer userId = userRepository.findByEmail(email).orElseThrow().getUserId();
        PasswordResetToken passwordResetToken = tokenResetRepository.findByUserId(userId).orElseThrow();

        boolean matches = Objects.equals(passwordResetToken.getToken(), token);

        if (matches) {
            tokenResetRepository.delete(passwordResetToken);
        }

        return matches;
    }

    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        List<UserResponseDTO> dtos = new ArrayList<>();
        users.forEach((user) -> {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setEmail(user.getEmail());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setRole(user.getRole());

            dtos.add(dto);
        });

        return dtos;
    }

    public boolean deleteByEmail(String email) {
        User toDelete = userRepository.findByEmail(email).orElseThrow();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", toDelete.getUserId());

        jdbcTemplate.update("delete from token where user_id = :id", params);
        userRepository.delete(toDelete);

        return true;
    }

}
