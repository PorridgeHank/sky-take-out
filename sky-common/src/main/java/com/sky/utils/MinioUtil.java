package com.sky.utils;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayInputStream;
@Data
@AllArgsConstructor
@Slf4j
public class MinioUtil {

    // Minio客户端单例
    private final String endpoint;  // 必须声明
    private final MinioClient minioClient;
    private final String bucketName;

    /**
     * 带参构造方法（推荐Spring注入使用）
     */
    public MinioUtil(String endpoint, String accessKey, String secretKey, String bucketName) {
        // 去除 endpoint 末尾可能存在的斜杠
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        // 必须将参数赋值给成员变量
        this.endpoint = endpoint;  // 关键赋值操作

        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
    }

    /**
     * 文件上传
     */
    public String upload(byte[] bytes, String objectName) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            // 自动创建Bucket（按需开启）
            createBucketIfMissing();

            // 执行上传
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(bis, bytes.length, -1)
                            .contentType("application/octet-stream")
                            .build());

            // 生成访问地址
            String url = generateUrl(objectName);
            log.info("文件成功上传至: {}", url);
            return url;
        } catch (Exception e) {
            handleUploadException(e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 自动创建Bucket（可选）
     */
    private void createBucketIfMissing() throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.warn("检测到自动创建存储桶: {}", bucketName);
        }
    }

    /**
     * 生成访问URL
     */
    private String generateUrl(String objectName) {
        return String.format("%s/%s/%s",
                endpoint,  // 使用保存的endpoint
                bucketName,
                objectName);
    }
    /**
     * 统一异常处理
     */
    private void handleUploadException(Exception e) {
        String errorMsg = "文件上传异常: ";
        if (e instanceof ErrorResponseException) {
            errorMsg += "Minio服务响应错误 - " + ((ErrorResponseException) e).errorResponse().message();
        } else if (e instanceof InsufficientDataException) {
            errorMsg += "数据流异常中断";
        } else if (e instanceof InternalException) {
            errorMsg += "Minio内部通信错误";
        } else {
            errorMsg += "未知错误类型";
        }
        log.error(errorMsg, e);
    }
}