package com.sky.config;

import com.sky.properties.MinioProperties;
import com.sky.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MinioUtil minioUtil(MinioProperties minioProperties){
        log.info("开始创建阿里云服务器Minio上传工具类对象：{}", minioProperties);
        return new MinioUtil(minioProperties.getEndpoint(),
                minioProperties.getAccessKey(),
                minioProperties.getSecretKey(),
                minioProperties.getBucketName());
    }
}
