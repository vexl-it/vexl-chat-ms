package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.common.cryptolib.CLibrary;
import com.cleevio.vexl.module.inbox.dto.request.CreateInboxRequest;
import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.exception.DuplicatedPublicKeyException;
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

        String publicKeyHash = CLibrary.CRYPTO_LIB.sha256_hash(request.publicKey(), request.publicKey().length());

        if (this.inboxRepository.existsByPublicKey(publicKeyHash)) {
            log.warn("Inbox [{}] already exists", publicKeyHash);
            throw new DuplicatedPublicKeyException();
        }

        Inbox inbox = createInboxEntity(request, publicKeyHash);
        Inbox savedInbox = this.inboxRepository.save(inbox);
        log.info("New inbox has been created with [{}]", savedInbox);
    }

    private Inbox createInboxEntity(CreateInboxRequest request, String publicKeyHash) {
        return Inbox.builder()
                .publicKey(publicKeyHash)
                .token(request.token())
                .build();
    }
}
