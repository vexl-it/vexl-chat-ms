package com.cleevio.vexl.module.temp;

public record TempRequest (

    String publicKey,
    String privateKey,
    String challenge

) {
}
