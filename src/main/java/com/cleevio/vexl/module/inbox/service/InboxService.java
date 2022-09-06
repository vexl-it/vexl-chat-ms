package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.common.constant.ModuleLockNamespace;
import com.cleevio.vexl.common.service.AdvisoryLockService;
import com.cleevio.vexl.module.inbox.constant.InboxAdvisoryLock;
import com.cleevio.vexl.module.inbox.constant.Platform;
import com.cleevio.vexl.module.inbox.dto.request.CreateInboxRequest;
import com.cleevio.vexl.module.inbox.dto.request.DeletionRequest;
import com.cleevio.vexl.module.inbox.dto.request.UpdateInboxRequest;
import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.exception.DuplicatedPublicKeyException;
import com.cleevio.vexl.module.inbox.exception.InboxNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class InboxService {

    private final InboxRepository inboxRepository;
    private final MessageService messageService;
    private final AdvisoryLockService advisoryLockService;

    @Transactional
    public void createInbox(@Valid CreateInboxRequest request, @NotNull Platform platform) {
        advisoryLockService.lock(
                ModuleLockNamespace.INBOX,
                InboxAdvisoryLock.CREATE_INBOX.name(),
                request.publicKey()
        );

        log.info("Creating inbox");

        if (this.inboxRepository.existsByPublicKey(request.publicKey())) {
            log.warn("Inbox [{}] already exists", request.publicKey());
            throw new DuplicatedPublicKeyException();
        }

        final Inbox inbox = createInboxEntity(request, request.publicKey(), platform);
        final Inbox savedInbox = this.inboxRepository.save(inbox);
        log.info("New inbox has been created with [{}]", savedInbox);
    }

    @Transactional(readOnly = true)
    public Inbox findInbox(@NotBlank String publicKey) {
        log.debug("Looking for inbox [{}]", publicKey);
        return this.inboxRepository.findByPublicKey(publicKey)
                .orElseThrow(InboxNotFoundException::new);
    }

    @Transactional
    public void deleteInbox(@Valid final DeletionRequest request) {
        advisoryLockService.lock(
                ModuleLockNamespace.INBOX,
                InboxAdvisoryLock.MODIFYING_INBOX.name(),
                request.publicKey()
        );

        final Inbox inbox = findInbox(request.publicKey());

        this.messageService.deleteAllMessages(inbox);
        this.inboxRepository.delete(inbox);
    }

    @Transactional
    public Inbox updateInbox(@Valid final UpdateInboxRequest request) {
        advisoryLockService.lock(
                ModuleLockNamespace.INBOX,
                InboxAdvisoryLock.MODIFYING_INBOX.name(),
                request.publicKey()
        );
        Inbox inbox = findInbox(request.publicKey());

        inbox.setToken(request.token());
        return this.inboxRepository.save(inbox);
    }

    @Transactional
    public void deleteInvalidToken(final String inboxPublicKey, final String firebaseToken) {
        advisoryLockService.lock(
                ModuleLockNamespace.INBOX,
                InboxAdvisoryLock.MODIFYING_INBOX.name(),
                inboxPublicKey
        );

        this.inboxRepository.deleteInvalidToken(inboxPublicKey, firebaseToken);
    }

    private Inbox createInboxEntity(CreateInboxRequest request, String publicKeyHash,
                                    Platform platform) {
        return Inbox.builder()
                .publicKey(publicKeyHash)
                .token(request.token())
                .platform(platform)
                .build();
    }
}
