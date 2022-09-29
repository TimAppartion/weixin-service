package com.example.weixin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@MapperScan(basePackages = {"com.example.weixin.user.mapper"})
@EnableEurekaClient
@SpringBootApplication
public class WeiXinApplication {
    public static void main(String[] args){
        SpringApplication.run(WeiXinApplication.class);
    }
}