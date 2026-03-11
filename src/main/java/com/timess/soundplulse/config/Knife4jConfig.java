package com.timess.soundplulse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SoundPlulse 音乐播放器后端接口")
                        .version("v1.0")
                        .description("SoundPlulse 音乐播放器后端接口文档，包含用户管理、歌手管理、歌曲管理等接口"));
    }
}
