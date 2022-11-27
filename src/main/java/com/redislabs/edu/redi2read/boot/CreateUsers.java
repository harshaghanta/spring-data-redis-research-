package com.redislabs.edu.redi2read.boot;

import java.io.InputStream;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redislabs.edu.redi2read.models.Role;
import com.redislabs.edu.redi2read.models.User;
import com.redislabs.edu.redi2read.repositories.RoleRepository;
import com.redislabs.edu.redi2read.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Order(2)
@Component
public class CreateUsers implements CommandLineRunner {


    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        if(userRepository.count() == 0) {
            Role adminRole = roleRepository.findFirstByName("admin");
            Role customerRole = roleRepository.findFirstByName("customer");


            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {

            };
            InputStream inputStream = getClass().getResourceAsStream("/data/users/users.json");
            List<User> users = mapper.readValue(inputStream, typeReference);

            users.stream().forEach(user -> {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.addRole(customerRole);
                userRepository.save(user);
            });

            log.info(">>> " + users.size() + " users saved!");

            User adminUser = new User();
            adminUser.setName("Adminus Administradore");
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("Reindeer Flotilla"));
            adminUser.addRole(adminRole);
            userRepository.save(adminUser);
            log.info(">>>> Loaded user data & created users....");
        }
        
    }
    
}
