package com.cleevio.vexl.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

public record ErrorResponse(

        Collection<String> message,

        String code

) {
}
