package com.crypto_trader.api_server.presentation;

import com.crypto_trader.api_server.application.dto.UserInfoDto;
import com.crypto_trader.api_server.domain.entities.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/info")
    public UserInfoDto getUserInfos(@AuthenticationPrincipal(expression = "user") UserEntity user) {
        return UserInfoDto.from(user);
    }
}
