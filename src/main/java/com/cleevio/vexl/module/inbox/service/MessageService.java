package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    @Transactional(rollbackFor = Throwable.class)
    public Set<Message> retrieveMessages(Inbox inbox) {
        Set<Message> messages = inbox.getMessages();

        messages.forEach(m -> {
            m.setPulled(true);
            this.messageRepository.save(m);
        });

        return messages;
    }
}
