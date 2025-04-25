package com.example.TusasProject.service;

import com.example.TusasProject.entity.Opinion;
import org.springframework.stereotype.Service;

@Service
public interface OpinionService {
    void save(Opinion opinion);
}
