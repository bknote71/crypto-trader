package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.AuthService;
import com.crypto_trader.api_server.dto.UserInfoDto;
import com.crypto_trader.api_server.domain.entities.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/info")
    public UserInfoDto getUserInfos(@AuthenticationPrincipal(expression = "user") UserEntity user) {
        return authService.findByUsername(user.getUsername());
    }
}
