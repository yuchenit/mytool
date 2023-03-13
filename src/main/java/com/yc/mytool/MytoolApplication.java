package com.yc.mytool;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan(basePackages = "com.sifar.test.dao")
@EnableSwagger2
public class MytoolApplication {


    public static void main(String[] args) {
        SpringApplication.run(MytoolApplication.class, args);
    }

}

