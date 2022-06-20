package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.dto.request.CreateInboxRequest;
import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.exception.DuplicatedPublicKeyException;
import com.cleevio.vexl.module.inbox.exception.InboxNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InboxService {

    private final InboxRepository inboxRepository;

    @Transactional(rollbackFor = Throwable.class)
    public void createInbox(CreateInboxRequest request) {
        log.info("Creating inbox");

        if (this.inboxRepository.existsByPublicKey(request.publicKey())) {
            log.warn("Inbox [{}] already exists", request.publicKey());
            throw new DuplicatedPublicKeyException();
        }

        Inbox inbox = createInboxEntity(request, request.publicKey());
        Inbox savedInbox = this.inboxRepository.save(inbox);
        log.info("New inbox has been created with [{}]", savedInbox);
    }

    private Inbox createInboxEntity(CreateInboxRequest request, String publicKeyHash) {
        return Inbox.builder()
                .publicKey(publicKeyHash)
                .token(request.token())
                .build();
    }

    @Transactional(readOnly = true)
    public Inbox findInbox(String publicKey) {
        log.debug("Looking for inbox [{}]", publicKey);
        return this.inboxRepository.findByPublicKey(publicKey)
                .orElseThrow(InboxNotFoundException::new);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteInbox(Inbox inbox) {
        this.inboxRepository.delete(inbox);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Inbox updateInbox(Inbox inbox, String firebaseToken) {
        inbox.setToken(firebaseToken);
        return this.inboxRepository.save(inbox);
    }
}
