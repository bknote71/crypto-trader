package com.crypto_trader.api_server.auth;

import com.crypto_trader.api_server.domain.entities.UserEntity;
import com.crypto_trader.api_server.infra.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsDBService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;

    @Autowired
    public UserDetailsDBService(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new PrincipalUser(user);
    }
}
