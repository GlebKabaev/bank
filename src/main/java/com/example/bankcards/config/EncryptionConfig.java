package com.example.bankcards.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionConfig {

    @Bean
    public byte[] cardEncryptionKey(@Value("${springdoc.card-encryption-key}") String key) {
        return key.getBytes();
    }
}
