package com.jim.ojbackendquestionservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;

    @Bean
    public RedissonClient redisClient(){
        Config redissonConfig = new Config();
        String address = String.format("redis://%s:%s",host,port);
        redissonConfig.useSingleServer().setAddress(address).setDatabase(1);
        return Redisson.create(redissonConfig);
    }
}