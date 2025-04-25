package com.example.TusasProject.controller;

import com.example.TusasProject.entity.User;
import com.example.TusasProject.service.UserService;

import com.example.TusasProject.service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {


    @Autowired
    UserService userService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public void registerUser(@RequestBody User user) {
        userService.save(user);
    }
}
