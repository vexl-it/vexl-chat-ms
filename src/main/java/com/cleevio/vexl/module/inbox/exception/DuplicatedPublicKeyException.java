package com.cleevio.vexl.module.inbox.exception;

import com.cleevio.vexl.common.exception.ApiException;
import com.cleevio.vexl.common.exception.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedPublicKeyException extends ApiException {

	@Override
	protected ApiException.Module getModule() {
		return Module.INBOX;
	}

	@Override
	protected ErrorType getErrorType() {
		return InboxErrorType.DUPLICATED_PUBLIC_KEY;
	}
}