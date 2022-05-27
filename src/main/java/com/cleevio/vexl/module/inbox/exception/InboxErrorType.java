package com.cleevio.vexl.module.inbox.exception;

import com.cleevio.vexl.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InboxErrorType implements ErrorType {

    DUPLICATED_PUBLIC_KEY("100", "Public key is already used. You cannot create Inbox again."),
    INBOX_NOT_FOUND("101", "Inbox with sent public key does not exist. Create the inbox first."),
    ;

    /**
     * Error custom code
     */
    private final String code;

    /**
     * Error custom message
     */
    private final String message;
}
