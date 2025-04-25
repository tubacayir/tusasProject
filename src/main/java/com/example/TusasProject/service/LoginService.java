package com.example.TusasProject.service;

import com.example.TusasProject.dto.LoginExpertDTO;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    boolean check(LoginExpertDTO loginExpertDTO);
}
