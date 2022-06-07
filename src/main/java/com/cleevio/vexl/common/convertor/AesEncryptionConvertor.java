package com.cleevio.vexl.common.convertor;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nullable;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AesEncryptionConvertor implements AttributeConverter<String, String> {

    @Value("${security.aes.key}")
    protected String key;

    @Nullable
    @Override
    public String convertToDatabaseColumn(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return CLibrary.CRYPTO_LIB.aes_encrypt(key, value);
    }

    @Nullable
    @Override
    public String convertToEntityAttribute(@Nullable String value) {
        if (value == null) {
            return null;
        }
        return CLibrary.CRYPTO_LIB.aes_decrypt(key, value);
    }
}
