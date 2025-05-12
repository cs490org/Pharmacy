package com.cs490.group4.security.config.token;

import com.cs490.group4.security.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    public Integer id;

    @Column(unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.HTTP_ONLY_COOKIE;

    public boolean revoked;

    public boolean expired;

    @ManyToOne
    @JoinColumn(referencedColumnName = "user_id")
    public User user;
}