package com.tus.users.config;

import com.tus.users.entity.Role;
import com.tus.users.entity.User;
import com.tus.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {

            if (userRepository.findByUsername("admin") == null) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin"); // OK for demo
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                System.out.println("✅ Admin user created: admin/admin");
            }
        };
    }
}