package com.cleevio.vexl.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private Collection<String> message;

    private String code;

    private Map<String, String> params;

}
