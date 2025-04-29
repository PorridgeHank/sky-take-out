package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {
    @Bean
    //因为pom文件里已经引入了Redis的spring框架依赖，starter起步依赖会将RedisConnectionFactory工厂对象创建好并放到spring容器中
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        log.info("开始创建redis模板对象...");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis的链接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化器。如果注释掉也可以使用，会使用默认序列化器
        //使用StringRedisSerializer序列化器效果是，redis数据库key的值可以展示为string类型数据，而非二进制代码
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
