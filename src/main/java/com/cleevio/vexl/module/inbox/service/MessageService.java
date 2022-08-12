package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.common.constant.ModuleLockNamespace;
import com.cleevio.vexl.common.service.AdvisoryLockService;
import com.cleevio.vexl.module.inbox.constant.MessageAdvisoryLock;
import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Message;
import com.cleevio.vexl.module.inbox.constant.MessageType;
import com.cleevio.vexl.module.inbox.constant.WhitelistState;
import com.cleevio.vexl.module.inbox.event.NewMessageReceivedEvent;
import com.cleevio.vexl.module.inbox.exception.RequestMessagingNotAllowedException;
import com.cleevio.vexl.module.inbox.exception.WhiteListException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final WhitelistService whitelistService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AdvisoryLockService advisoryLockService;

    @Transactional
    public List<Message> retrieveMessages(Inbox inbox) {
        advisoryLockService.lock(
                ModuleLockNamespace.MESSAGE,
                MessageAdvisoryLock.RETRIEVE_MESSAGE.name(),
                inbox.getPublicKey()
        );

        List<Message> messages = inbox.getMessages()
                .stream()
                .sorted(Comparator.comparing(Message::getId))
                .toList();

        messages.forEach(m -> {
            m.setPulled(true);
            this.messageRepository.save(m);
        });

        return messages;
    }

    @Transactional
    public void deletePulledMessages(Inbox inbox) {
        advisoryLockService.lock(
                ModuleLockNamespace.MESSAGE,
                MessageAdvisoryLock.DELETE_MESSAGE.name(),
                inbox.getPublicKey()
        );

        this.messageRepository.deleteAllPulledMessages(inbox);
    }

    @Transactional
    public void sendMessageToInbox(String senderPublicKey, String receiverPublicKey, Inbox receiverInbox, String message, MessageType messageType) {
        advisoryLockService.lock(
                ModuleLockNamespace.MESSAGE,
                MessageAdvisoryLock.SEND_MESSAGE.name(),
                receiverPublicKey, senderPublicKey
        );

        if (!this.whitelistService.isSenderInWhitelistApproved(senderPublicKey, receiverInbox)) {
            log.info("Sender [{}] is blocked by receiver [{}] or not approve yet.", senderPublicKey, receiverInbox);
            throw new WhiteListException();
        }

        this.saveMessageToInboxAndSendNotification(senderPublicKey, receiverPublicKey, receiverInbox, message, messageType);
    }

    @Transactional
    public void sendRequestToPermission(String senderPublicKey, String receiverPublicKey, Inbox receiverInbox, String message) {
        advisoryLockService.lock(
                ModuleLockNamespace.MESSAGE,
                MessageAdvisoryLock.SEND_MESSAGE.name(),
                receiverPublicKey, senderPublicKey
        );

        if (this.whitelistService.isSenderInWhitelist(senderPublicKey, receiverInbox)) {
            log.warn("Sender [{}] has already sent a request for permission to messaging for inbox [{}]", senderPublicKey, receiverInbox);
            throw new RequestMessagingNotAllowedException();
        }

        this.whitelistService.createWhiteListEntity(receiverInbox, senderPublicKey, WhitelistState.WAITING);

        this.saveMessageToInboxAndSendNotification(senderPublicKey, receiverPublicKey, receiverInbox, message, MessageType.REQUEST_MESSAGING);
    }

    @Transactional
    public void sendDisapprovalMessage(String senderPublicKey, String receiverPublicKey, Inbox receiverInbox, String message) {
        advisoryLockService.lock(
                ModuleLockNamespace.MESSAGE,
                MessageAdvisoryLock.SEND_MESSAGE.name(),
                receiverPublicKey, senderPublicKey
        );

        this.saveMessageToInboxAndSendNotification(senderPublicKey, receiverPublicKey, receiverInbox, message, MessageType.DISAPPROVE_MESSAGING);
    }

    private void saveMessageToInboxAndSendNotification(String senderPublicKey, String receiverPublicKey, Inbox receiverInbox, String message, MessageType messageType) {

        final Message messageEntity = createMessageEntity(senderPublicKey, receiverInbox, message, messageType);
        this.messageRepository.save(messageEntity);

        if (receiverInbox.getToken() == null) return;
        this.applicationEventPublisher.publishEvent(new NewMessageReceivedEvent(receiverInbox.getToken(), messageType, receiverPublicKey, senderPublicKey));
    }

    private Message createMessageEntity(String senderPublicKey, Inbox receiverInbox, String message, MessageType messageType) {
        return Message.builder()
                .message(message)
                .inbox(receiverInbox)
                .senderPublicKey(senderPublicKey)
                .type(messageType)
                .build();
    }

    @Transactional
    public void deleteAllMessages(Inbox inbox) {
        advisoryLockService.lock(
                ModuleLockNamespace.MESSAGE,
                MessageAdvisoryLock.DELETE_ALL_MESSAGES.name(),
                inbox.getPublicKey()
        );

        this.messageRepository.deleteAllMessages(inbox);
    }
}
