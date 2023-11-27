package com.example.Blog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailsManager.setUsersByUsernameQuery("select username, password, is_active from users where username=?");
        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select u.username, r.name from users u " +
                "join roles r on u.role_id=r.id where u.username=?");
        return jdbcUserDetailsManager;

    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(configure -> configure
                        .requestMatchers("/register", "/login").permitAll()
                        .anyRequest().authenticated()
        ).formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/authenticateTheUser")
                .permitAll()
                .defaultSuccessUrl("/")
        ).logout(logout -> logout.permitAll()
        ).exceptionHandling(configure -> configure.accessDeniedPage("/access_denied")

        );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
