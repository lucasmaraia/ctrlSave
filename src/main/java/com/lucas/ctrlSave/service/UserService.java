package com.lucas.ctrlSave.service;

import com.lucas.ctrlSave.model.entity.User;
import com.lucas.ctrlSave.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User getUserAuthenticated(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
           return null;
        }

        String username;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByUsername(username).orElse(null);
    }

    public User saveUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Usuário já existe!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("user");
        return userRepository.save(user);
    }

}

