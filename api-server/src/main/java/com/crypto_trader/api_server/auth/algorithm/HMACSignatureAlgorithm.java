package com.crypto_trader.api_server.auth.algorithm;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
public class HMACSignatureAlgorithm implements SignAlgorithm {

    private final SecretKey secretKey;

    public HMACSignatureAlgorithm() {
        // 암호화된 환경 변수 혹은 키 관리 시스템에서 가져와야 한다.
        String secret = "secretsecretsecretsecretsecretsecretsecretsecret"; // size >= 256 (안되면 에러)
        byte[] bytes = secret.getBytes();
        assert bytes.length >= 32;
        this.secretKey = new SecretKeySpec(bytes, 0, bytes.length, "HmacSHA256");
    }

    @Override
    public Key encodeKey() {
        return this.secretKey;
    }

    @Override
    public Key decodeKey() {
        return this.secretKey;
    }
}
