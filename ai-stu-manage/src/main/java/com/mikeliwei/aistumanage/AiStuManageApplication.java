package com.mikeliwei.aistumanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.mikeliwei.aistumanage.mapper")
public class AiStuManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiStuManageApplication.class, args);
    }

}
