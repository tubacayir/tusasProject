package com.example.TusasProject.service;

import com.example.TusasProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class UserModelService {

    private final UserRepository userRepository;

    public void addUserInfoToModel(Model model, String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            model.addAttribute("userName", user.getFirstName() + " " + user.getLastName());
            model.addAttribute("userRole", user.getRole().getName());
            model.addAttribute("userExpertise", user.getExpertise());
        });
    }
}