package com.cleevio.vexl.common.validator;

import com.cleevio.vexl.common.annotation.CheckAllowedMessageType;
import com.cleevio.vexl.module.inbox.enums.MessageType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static com.cleevio.vexl.module.inbox.enums.MessageType.APPROVE_MESSAGING;
import static com.cleevio.vexl.module.inbox.enums.MessageType.DISAPPROVE_MESSAGING;
import static com.cleevio.vexl.module.inbox.enums.MessageType.REQUEST_MESSAGING;

public class AllowedMessageTypeValidator implements ConstraintValidator<CheckAllowedMessageType, MessageType> {

    private final static List<MessageType> NOT_ALLOWED_MESSAGE_TYPES = List.of(DISAPPROVE_MESSAGING, REQUEST_MESSAGING, APPROVE_MESSAGING);

    @Override
    public void initialize(CheckAllowedMessageType constraintAnnotation) {
    }

    @Override
    public boolean isValid(MessageType type, ConstraintValidatorContext constraintContext) {
        return type != null && !NOT_ALLOWED_MESSAGE_TYPES.contains(type);
    }
}
