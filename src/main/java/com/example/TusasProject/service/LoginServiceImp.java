package com.example.TusasProject.service;

import com.example.TusasProject.dto.LoginExpertDTO;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImp implements LoginService {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean check(LoginExpertDTO loginExpertDTO) {
        User user = userRepository.findByEmail(loginExpertDTO.getEmail());
        if (loginExpertDTO.getPassword().equals(user.getPassword()) ) {
            return true;
        }
       return false;

    }
}
