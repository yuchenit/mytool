package com.yc.mytool.common.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author:yuchen
 * @createTime:2023/3/13 19:17
 */
@Component
public class RedisService {
    public void setCacheObject(String s, String host, long l, TimeUnit days) {
    }
    public void setCacheObject(String s, String host) {
    }


    public String getCacheObject(String s) {
        return "";
    }
}
