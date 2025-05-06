package com.example.TusasProject.service;

import com.example.TusasProject.entity.Role;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.repository.RoleRepository;
import com.example.TusasProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username) // email ile sorgulama yapılacak
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail()) // email ile giriş yapılacak
                .password(user.getPassword()) // şifreyi doğru şekilde al
                .roles(user.getRole().getName())
                .build();
    }

    // Kullanıcıyı kaydetme işlemi
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));  // Şifreyi güvenli şekilde şifrele

        // Eğer kullanıcıya rol atanmadıysa, varsayılan olarak "EXPERT" rolünü ver
        if (user.getRole() == null) {
            Role expertRole = roleRepository.findByName("EXPERT")
                    .orElseThrow(() -> new RuntimeException("Role 'EXPERT' bulunamadı"));
            user.setRole(expertRole);
        }

        userRepository.save(user);  // Kullanıcıyı veritabanına kaydet
    }
}
