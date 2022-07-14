package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Message;
import com.cleevio.vexl.module.inbox.enums.MessageType;
import com.cleevio.vexl.module.inbox.enums.WhitelistState;
import com.cleevio.vexl.module.inbox.event.PushNotificationEvent;
import com.cleevio.vexl.module.inbox.exception.RequestMessagingNotAllowedException;
import com.cleevio.vexl.module.inbox.exception.WhiteListException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final WhitelistService whitelistService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(rollbackFor = Throwable.class)
    public List<Message> retrieveMessages(Inbox inbox) {
        List<Message> messages = inbox.getMessages()
                .stream()
                .sorted(Comparator.comparing(Message::getId))
                .collect(Collectors.toList());

        messages.forEach(m -> {
            m.setPulled(true);
            this.messageRepository.save(m);
        });

        return messages;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deletePulledMessages(Inbox inbox) {
        this.messageRepository.deleteAllPulledMessages(inbox);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void sendMessageToInbox(String senderPublicKey, String receiverPublicKey, Inbox receiverInbox, String message, MessageType messageType) {

        if (!this.whitelistService.isSenderInWhitelistApproved(senderPublicKey, receiverInbox)) {
            log.info("Sender [{}] is blocked by receiver [{}] or not approve yet.", senderPublicKey, receiverInbox);
            throw new WhiteListException();
        }

        this.saveMessageToInboxAndSendNotification(senderPublicKey, receiverPublicKey, receiverInbox, message, messageType);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void sendRequestToPermission(String senderPublicKey, String receiverPublicKey, Inbox receiverInbox, String message) {
        if (this.whitelistService.isSenderInWhitelist(senderPublicKey, receiverInbox)) {
            log.warn("Sender [{}] has already sent a request for permission to messaging for inbox [{}]", senderPublicKey, receiverInbox);
            throw new RequestMessagingNotAllowedException();
        }

        this.whitelistService.createWhiteListEntity(receiverInbox, senderPublicKey, WhitelistState.WAITING);

        this.saveMessageToInboxAndSendNotification(senderPublicKey, receiverPublicKey, receiverInbox, message, MessageType.REQUEST_MESSAGING);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void sendDisapprovalMessage(String senderPublicKey, String receiverPublicKey,  Inbox receiverInbox, String message) {
        this.saveMessageToInboxAndSendNotification(senderPublicKey, receiverPublicKey, receiverInbox, message, MessageType.DISAPPROVE_MESSAGING);
    }

    private void saveMessageToInboxAndSendNotification(String senderPublicKey, String receiverPublicKey, Inbox receiverInbox, String message, MessageType messageType) {

        final Message messageEntity = createMessageEntity(senderPublicKey, receiverInbox, message, messageType);
        this.messageRepository.save(messageEntity);

        if (receiverInbox.getToken() == null) return;
        this.applicationEventPublisher.publishEvent(new PushNotificationEvent(receiverInbox.getToken(), messageType, receiverPublicKey));
    }

    private Message createMessageEntity(String senderPublicKey, Inbox receiverInbox, String message, MessageType messageType) {
        return Message.builder()
                .message(message)
                .inbox(receiverInbox)
                .senderPublicKey(senderPublicKey)
                .type(messageType)
                .build();
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteAllMessages(Inbox inbox) {
        this.messageRepository.deleteAllMessages(inbox);
    }
}
