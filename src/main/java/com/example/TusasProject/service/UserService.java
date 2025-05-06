package com.example.TusasProject.service;

import com.example.TusasProject.entity.Role;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.repository.RoleRepository;
import com.example.TusasProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Eğer kullanıcıya rol atanmadıysa default olarak EXPERT ver
        if (user.getRole() == null) {
            Role expertRole = roleRepository.findByName("EXPERT")
                    .orElseThrow(() -> new RuntimeException("Role 'EXPERT' bulunamadı"));
            user.setRole(expertRole);
        }

        userRepository.save(user);
    }
}