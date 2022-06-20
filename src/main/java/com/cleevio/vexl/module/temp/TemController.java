package com.cleevio.vexl.module.temp;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "TEMP - only for testing purposes")
@RestController
@RequestMapping("/api/v1/temp")
@RequiredArgsConstructor
public class TemController {

    @PostMapping
    @Operation(summary = "ECDSA verifying", description = "Only for testing purposes")
    public TempResponse signItTemp(@RequestBody TempRequest request) {
        return new TempResponse(CLibrary.CRYPTO_LIB.ecdsa_sign(request.publicKey(), request.privateKey(), request.challenge(), request.challenge().length()));
    }
}
