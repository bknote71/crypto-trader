package com.crypto_trader.api_server.auth;

import com.crypto_trader.api_server.domain.entities.UserEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class PrincipalUser extends User {

    private final UserEntity user;

    public PrincipalUser(UserEntity user) {
        // A granted authority textual representation is required (유효환 권한 이름 제공. 빈 문자열 방지)
        super(user.getUsername(), user.getUsername(), List.of(new SimpleGrantedAuthority("그냥아무권한:ROLE_으로시작하는것이권장됨")));
        this.user = user;
    }

    // for expression Property
    public UserEntity getUser() {
        return user;
    }
}
