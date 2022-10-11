package com.example.jiuzhou;


//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = {"com.example.jiuzhou.user.mapper"})
@EnableEurekaClient
@SpringBootApplication
@EnableCaching
public class JiuZhouApplication {
    public static void main(String[] args){
        SpringApplication.run(JiuZhouApplication.class);
    }
}