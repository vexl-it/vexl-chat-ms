package com.cleevio.vexl.module.challenge.exception;

import com.cleevio.vexl.common.exception.ApiException;
import com.cleevio.vexl.common.exception.ErrorType;

public class ChallengeMissingException extends ApiException {

	@Override
	protected ApiException.Module getModule() {
		return Module.CHALLENGE;
	}

	@Override
	protected ErrorType getErrorType() {
		return ChallengeErrorType.CHALLENGE_MISSING;
	}
}
