package com.example.bankcards.config;

import com.example.bankcards.util.CryptoUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionBeans {
    @Bean
    public CryptoUtil cryptoUtil(byte[] cardEncryptionKey) {
        return new CryptoUtil(cardEncryptionKey);
    }
}