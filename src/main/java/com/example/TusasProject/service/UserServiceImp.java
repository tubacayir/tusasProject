package com.example.TusasProject.service;


import com.example.TusasProject.entity.User;
import com.example.TusasProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    UserRepository userRepository;


    public void save(User user) {
    userRepository.save(user);
    }
}
