package com.cleevio.vexl.module.chat.exception;

import com.cleevio.vexl.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatErrorType implements ErrorType {

    CHANNEL_ERROR("100", "Couldn't create or ger channel for communication."),
    SENDING_MESSAGE_ERROR("101", "Error during sending message."),
    GETTING_MESSAGE_ERROR("102", "Error during getting message.");

    /**
     * Error custom code
     */
    private final String code;

    /**
     * Error custom message
     */
    private final String message;
}
