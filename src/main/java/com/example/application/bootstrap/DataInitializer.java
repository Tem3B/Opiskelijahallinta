package com.example.application.bootstrap;

import com.example.application.data.User;
import com.example.application.services.UserService;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            createIfMissing(userService, passwordEncoder, "Admin", "admin123", Set.of("admin", "super"));
            createIfMissing(userService, passwordEncoder, "User", "user123", Set.of("user"));
        };
    }

    private void createIfMissing(UserService userService, PasswordEncoder encoder, String username, String password, Set<String> roles) {
        if (userService.findByUsername(username).isEmpty()) {
            User u = new User();
            u.setUsername(username);
            u.setPassword(encoder.encode(password)); // store hashed password
            u.setRoles(roles);
            userService.save(u);
            System.out.println("Created user: " + username);
        }
    }
}

