package com.cleevio.vexl.module.chat.service;

import com.cleevio.vexl.module.chat.dto.request.MessageRequest;
import com.cleevio.vexl.module.chat.exception.ChannelException;
import com.cleevio.vexl.module.chat.exception.GettingMessagesException;
import com.cleevio.vexl.module.chat.exception.SendingMessageException;
import io.getstream.chat.java.exceptions.StreamException;
import io.getstream.chat.java.models.Channel;
import io.getstream.chat.java.models.Message;
import io.getstream.chat.java.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class ChatService {

    @Transactional(readOnly = true)
    public void sendMessage(MessageRequest request)
            throws ChannelException, SendingMessageException {
        log.info("Sending message from {} to {} ",
                request.getUserPublicKey(),
                request.getReceiverPublicKey());

        String channelId = createChannelId(request.getUserPublicKey(), request.getReceiverPublicKey());

        Channel.ChannelGetResponse channel = getOrCreateChannel(request.getUserPublicKey(), request.getReceiverPublicKey(), channelId);

        sendMessage(request.getEncryptedMessage(),
                request.getUserPublicKey(),
                channel.getChannel().getType(),
                channelId);
    }

    private String createChannelId(String userPublicKey, String receiverPublicKey) {
        return String.join("", Stream.of(userPublicKey, receiverPublicKey)
                .sorted().toList());
    }

    private Channel.ChannelGetResponse getOrCreateChannel(String userPublicKey, String receiverPublicKey, String channelId)
            throws ChannelException {

        try {
            return Channel.getOrCreate("messaging", channelId)
                    .data(
                            Channel.ChannelRequestObject.builder()
                                    .member(Channel.ChannelMemberRequestObject.builder().userId(userPublicKey).build())
                                    .member(Channel.ChannelMemberRequestObject.builder().userId(receiverPublicKey).build())
                                    .createdBy(User.UserRequestObject.builder().id(userPublicKey).build())
                                    .build())
                    .request();
        } catch (StreamException e) {
            log.error("Error occurred during creating or getting channel", e);
            throw new ChannelException();
        }
    }

    private Message.MessageSendResponse sendMessage(String message, String userPublicKey, String channelType, String channelId)
            throws SendingMessageException {
        try {
            return Message.send(channelType, channelId)
                    .message(
                            Message.MessageRequestObject.builder()
                                    .text(
                                            message
                                    )
                                    .userId(userPublicKey)
                                    .build())
                    .request();
        } catch (StreamException e) {
            log.error("Error occurred during sending message", e);
            throw new SendingMessageException();
        }
    }

    @Transactional(readOnly = true)
    public List<Message> receiveMessageFromChannel(String channelId)
            throws GettingMessagesException {
        try {
            Channel.ChannelGetResponse channel = Channel.getOrCreate(channelId).request();
            return channel.getMessages();

        } catch (StreamException e) {
            log.error("Error occurred while getting messages", e);
            throw new GettingMessagesException();
        }
    }
}
