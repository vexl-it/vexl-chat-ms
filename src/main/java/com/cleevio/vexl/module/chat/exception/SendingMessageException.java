package com.cleevio.vexl.module.chat.exception;

import com.cleevio.vexl.common.exception.ApiException;
import com.cleevio.vexl.common.exception.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class SendingMessageException extends ApiException {

    @Override
    protected ApiException.Module getModule() {
        return ApiException.Module.CHAT;
    }

    @Override
    protected ErrorType getErrorType() {
        return ChatErrorType.SENDING_MESSAGE_ERROR;
    }
}
