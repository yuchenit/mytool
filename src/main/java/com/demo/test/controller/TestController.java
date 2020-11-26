package com.demo.test.controller;

import com.demo.test.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping("/senMsg")
    public String sendMsg(){

        String result = testService.sendMsg();

        return result;
    }
}
