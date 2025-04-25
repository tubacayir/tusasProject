package com.example.TusasProject.service;

import com.example.TusasProject.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {


    void save(User user);
}
