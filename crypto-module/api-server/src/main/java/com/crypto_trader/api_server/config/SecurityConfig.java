package com.crypto_trader.api_server.config;

import com.crypto_trader.api_server.auth.JwtRequestFilter;
import com.crypto_trader.api_server.auth.JwtUtil;
import com.crypto_trader.api_server.auth.PrincipalUser;
import com.crypto_trader.api_server.auth.UserDetailsDBService;
import com.crypto_trader.api_server.domain.entities.CryptoAsset;
import com.crypto_trader.api_server.domain.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsDBService userDetailsDBService;
    private final JwtUtil jwtUtil;

    private static final String[] WHITELIST = {
            "/login/**", "/logout/**", "/signup/**", "/h2-console/**"
    };

    @Autowired
    public SecurityConfig(UserDetailsDBService userDetailsDBService, JwtUtil jwtUtil) {
        this.userDetailsDBService = userDetailsDBService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(new JwtRequestFilter(userDetailsDBService, jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(WHITELIST).permitAll()
                .anyRequest().permitAll()
        );

        // for anonymous
//        http.anonymous(anon -> anon.principal(createAnonymousPrincipal()));

        return http.build();
    }

    private PrincipalUser createAnonymousPrincipal() {
        var user = new UserEntity("anonymous");
        return new PrincipalUser(user);
    }

    // for h2
    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled",havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }
}
