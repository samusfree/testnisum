package com.nisum.testnisum.configuration;


import com.nisum.testnisum.data.entity.User;
import com.nisum.testnisum.data.repository.UserRepository;
import com.nisum.testnisum.mappers.UserMapper;
import com.nisum.testnisum.security.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JWTFilter jwtFilter;
    private final UserMapper userMapper;

    public SecurityConfig(UserRepository userRepository, JWTFilter jwtFilter, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.jwtFilter = jwtFilter;
        this.userMapper = userMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.requestMatchers("/swagger-doc/**").permitAll().requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll().anyRequest().authenticated());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
            return userMapper.customUserDetailsFromUser(user);
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
