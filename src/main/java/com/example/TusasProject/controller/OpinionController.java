package com.example.TusasProject.controller;


import com.example.TusasProject.entity.Opinion;
import com.example.TusasProject.service.OpinionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OpinionController {

    @Autowired
    OpinionService opinionService;

    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    @ResponseBody
    public void registerUser(@RequestBody Opinion opinion) {
        opinionService.save(opinion);
    }
}