package com.example.TusasProject.service;

import com.example.TusasProject.dto.LoginExpertDTO;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImp implements LoginService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Override
    public boolean check(LoginExpertDTO loginExpertDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginExpertDTO.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return passwordEncoder.matches(loginExpertDTO.getPassword(), user.getPassword());
        }

        return false;
    }
}
