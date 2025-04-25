package com.example.TusasProject.service;

import com.example.TusasProject.entity.Opinion;
import com.example.TusasProject.repository.OpinionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpinionServiceImp implements OpinionService {
    @Autowired
    OpinionRepository OpinionRepository;

    @Override
    public void save(Opinion opinion) {
        OpinionRepository.save(opinion);
    }
}
