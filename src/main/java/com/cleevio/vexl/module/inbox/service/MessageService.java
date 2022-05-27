package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Message;
import com.cleevio.vexl.module.inbox.exception.AllowanceRequestNotAllowedException;
import com.cleevio.vexl.module.inbox.exception.WhiteListException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.cleevio.vexl.common.service.CryptoService.createHash256;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final WhitelistService whitelistService;

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
    public void sendMessageToInbox(String senderPublicKey, Inbox receiverInbox, String message) {
        String publicKeySenderHash = createHash256(senderPublicKey);

        if (!this.whitelistService.isSenderInWhitelistNotBlocked(publicKeySenderHash, receiverInbox)) {
            log.info("Sender [{}] is blocked by receiver [{}]", senderPublicKey, receiverInbox);
            throw new WhiteListException();
        }

        saveMessageToInboxAndSendNotification(senderPublicKey, receiverInbox, message);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void sendRequestToAllowance(String senderPublicKey, Inbox receiverInbox, String message) {
        if (this.messageRepository.alreadySentRequestAllowence(senderPublicKey, receiverInbox) ||
                this.whitelistService.isSenderInWhitelistBlocked(createHash256(senderPublicKey), receiverInbox)) {
            log.warn("Sender [{}] either sent request to allowance already or is blocked by inbox [{}]", senderPublicKey, receiverInbox);
            throw new AllowanceRequestNotAllowedException();
        }

        saveMessageToInboxAndSendNotification(senderPublicKey, receiverInbox, message);
    }

    private void saveMessageToInboxAndSendNotification(String senderPublicKey, Inbox receiverInbox, String message) {

        Message messageEntity = createMessageEntity(senderPublicKey, receiverInbox, message);
        this.messageRepository.save(messageEntity);
        //todo sent push notification
    }

    private Message createMessageEntity(String senderPublicKey, Inbox receiverInbox, String message) {
        return Message.builder()
                .message(message)
                .inbox(receiverInbox)
                .senderPublicKey(senderPublicKey)
                .build();
    }
}
