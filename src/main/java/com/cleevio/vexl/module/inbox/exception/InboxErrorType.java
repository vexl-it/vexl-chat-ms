package com.cleevio.vexl.module.inbox.exception;

import com.cleevio.vexl.common.exception.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InboxErrorType implements ErrorType {

    DUPLICATED_PUBLIC_KEY("100", "Public key is already used. You cannot create Inbox again."),
    INBOX_NOT_FOUND("101", "Inbox with sent public key does not exist. Create the inbox first."),
    WHITE_LIST_EXCEPTION("102", "You are not on whitelist. Either you are blocked or you have not yet been approved by the recipient to send messages."),
    ALLOWANCE_NOT_ALLOWED("103", "You cannot send request to allowance. Either you are blocked or you already have sent a request to the recipient."),
    MISSING_ON_WHITELIST("104", "Contact you want to block/unblock is not on your whitelist. In order to block someone, they must first get on your whitelist." +
            " They will get on the whitelist if you confirm it via allowance confirm EP."),
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
