package com.sky.config;


import com.aliyun.oss.common.utils.StringUtils;
import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于创建AliOssUtil对象
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean//交给容器管理对象创建
    @ConditionalOnMissingBean//只有当容器中没有对象时才创建，防止资源浪费
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {//通过参数调用对象，获取数据源
        log.info("开始创建阿里云文件上传工具类对象：{}",aliOssProperties);
        AliOssUtil aliOssUtil=new AliOssUtil(aliOssProperties.getEndpoint(),
                StringUtils.trim(System.getenv("OSS_ACCESS_KEY_ID")),
                StringUtils.trim(System.getenv("OSS_ACCESS_KEY_SECRET")),
                aliOssProperties.getBucketName());
            return aliOssUtil;
    }
}
