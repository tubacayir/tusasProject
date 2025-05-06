package com.example.TusasProject.controller;

import com.example.TusasProject.entity.Role;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.repository.RoleRepository;
import com.example.TusasProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }



    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> registerUserJson(@RequestBody User user) {
        Role role = roleRepository.findByName(user.getRole().getName()) // "EXPERT" ya da "MANAGER"
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı"));
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return ResponseEntity.ok("Kayıt başarılı");
    }
}
