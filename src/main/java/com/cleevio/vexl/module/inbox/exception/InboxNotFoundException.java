package com.cleevio.vexl.module.inbox.exception;

import com.cleevio.vexl.common.exception.ApiException;
import com.cleevio.vexl.common.exception.ErrorType;

public class InboxNotFoundException extends ApiException {

	@Override
	protected ApiException.Module getModule() {
		return Module.INBOX;
	}

	@Override
	protected ErrorType getErrorType() {
		return InboxErrorType.INBOX_NOT_FOUND;
	}
}
