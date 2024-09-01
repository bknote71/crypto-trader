package com.crypto_trader.api_server.auth.algorithm;

import com.crypto_trader.api_server.auth.RsaKeyUtil;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

// 이거 쓸 떼
// io.jsonwebtoken.lang.UnknownClassException: Unable to load class named [io.jsonwebtoken.impl.DefaultJwtBuilder] 이런 오류 발생 시
//  'org.bouncycastle : bcprov-jdk15on : 버전' 의존성 추가
public class RSASignAlgorithm implements SignAlgorithm {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RSASignAlgorithm() throws Exception {
        this.privateKey = RsaKeyUtil.getPrivateKey("");
        this.publicKey = RsaKeyUtil.getPublicKey("");
    }


    @Override
    public Key encodeKey() {
        return privateKey;
    }

    @Override
    public Key decodeKey() {
        return null;
    }
}
