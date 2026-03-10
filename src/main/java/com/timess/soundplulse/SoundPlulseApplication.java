package com.timess.soundplulse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.timess.soundplulse.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class SoundPlulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoundPlulseApplication.class, args);
    }

}
