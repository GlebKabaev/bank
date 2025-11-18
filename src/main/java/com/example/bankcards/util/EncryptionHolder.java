package com.example.bankcards.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class EncryptionHolder {

    @Getter
    private static CryptoUtil crypto;

    public EncryptionHolder(CryptoUtil crypto) {
        EncryptionHolder.crypto = crypto;
    }

}
