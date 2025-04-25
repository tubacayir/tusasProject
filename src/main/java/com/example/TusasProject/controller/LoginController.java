package com.example.TusasProject.controller;


import com.example.TusasProject.dto.LoginExpertDTO;
import com.example.TusasProject.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    @Autowired
    LoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public boolean login(@RequestBody LoginExpertDTO loginPersonalDTO) {
      boolean result =  loginService.check(loginPersonalDTO);
       return result;
    }
}
