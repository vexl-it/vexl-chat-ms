package com.cleevio.vexl.module.chat.exception;

import com.cleevio.vexl.common.exception.ApiException;
import com.cleevio.vexl.common.exception.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GettingMessagesException extends ApiException {

    @Override
    protected ApiException.Module getModule() {
        return Module.CHAT;
    }

    @Override
    protected ErrorType getErrorType() {
        return ChatErrorType.GETTING_MESSAGE_ERROR;
    }
}
