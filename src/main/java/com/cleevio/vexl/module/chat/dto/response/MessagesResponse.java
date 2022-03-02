package com.cleevio.vexl.module.chat.dto.response;

import io.getstream.chat.java.models.Message;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class MessagesResponse {

    private List<MessageResponse> messageResponses;

    public MessagesResponse(List<Message> messages) {
        messages.forEach(
                m -> {
                    messageResponses.add(new MessageResponse(m.getId(), m.getText(), m.getCreatedAt(), Objects.requireNonNull(m.getUser()).getId()));
                }
        );
    }
}
