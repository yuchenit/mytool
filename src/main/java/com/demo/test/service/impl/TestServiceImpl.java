package com.demo.test.service.impl;

import com.demo.test.service.TestService;
import org.springframework.stereotype.Service;


@Service
public class TestServiceImpl implements TestService {



    @Override
    public String sendMsg() {

        return "成功";
    }
}
