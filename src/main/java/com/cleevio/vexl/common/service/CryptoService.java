package com.cleevio.vexl.common.service;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {

    public static String createHash256(String value) {
        return CLibrary.CRYPTO_LIB.sha256_hash(value, value.length());
    }
}
