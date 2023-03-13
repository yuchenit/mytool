package com.yc.mytool.service.impl;

import com.yc.mytool.service.TestService;
import org.springframework.stereotype.Service;


@Service
public class TestServiceImpl implements TestService {



    @Override
    public String sendMsg() {

        return "成功";
    }
}
