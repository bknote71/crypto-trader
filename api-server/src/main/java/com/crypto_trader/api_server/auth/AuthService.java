package com.crypto_trader.api_server.auth;

import com.crypto_trader.api_server.domain.entities.UserEntity;
import com.crypto_trader.api_server.infra.UserEntityRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    private final UserEntityRepository userEntityRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserEntityRepository userEntityRepository, JwtUtil jwtUtil) {
        this.userEntityRepository = userEntityRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostConstruct
    public void init() {
        UserEntity user1 = new UserEntity("user1");
        UserEntity user2 = new UserEntity("user2");
        UserEntity user3 = new UserEntity("user3");
        userEntityRepository.saveAll(List.of(user1, user2, user3));
    }

    @Transactional
    public String login(String username) {
        userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return jwtUtil.generateToken(username);
    }
}
