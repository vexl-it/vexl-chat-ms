package com.cleevio.vexl.module.inbox.mapper;

import com.cleevio.vexl.module.inbox.dto.response.MessageResponse;
import com.cleevio.vexl.module.inbox.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {

    public MessageResponse mapSingle(Message message) {
        return new MessageResponse(
                message.getMessage(),
                message.getSenderPublicKey()
        );
    }

    public List<MessageResponse> mapList(List<Message> messages) {
        return messages.stream()
                .map(this::mapSingle)
                .collect(Collectors.toList());
    }
}
