package com.timess.soundpulse.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 歌词缓存配置
 */
@Configuration
@EnableCaching
public class LyricsCacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("lyrics");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)  // 缓存24小时
            .maximumSize(2000)                     // 最多缓存2000条歌词
            .recordStats()                         // 记录缓存统计信息
        );
        return cacheManager;
    }
}