package com.crypto_trader.api_server.auth.algorithm;

import java.security.Key;

public interface SignAlgorithm {
    Key encodeKey();

    Key decodeKey();
}
