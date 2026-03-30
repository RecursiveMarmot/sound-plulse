package com.timess.soundpluse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.timess.soundpluse.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class SoundPulseApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoundPulseApplication.class, args);
    }

}
