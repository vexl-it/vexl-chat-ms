package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.dto.request.BlockInboxRequest;
import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Whitelist;
import com.cleevio.vexl.module.inbox.enums.WhitelistState;
import com.cleevio.vexl.module.inbox.exception.AlreadyApprovedException;
import com.cleevio.vexl.module.inbox.exception.WhitelistMissingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class WhitelistService {

    private final WhitelistRepository whitelistRepository;

    public boolean isSenderInWhitelistApproved(String publicKeySenderHash, Inbox receiverInbox) {
        return this.whitelistRepository.isSenderInWhitelist(publicKeySenderHash, receiverInbox, WhitelistState.APPROVED);
    }

    public boolean isSenderInWhitelist(String publicKeySenderHash, Inbox receiverInbox) {
        return this.whitelistRepository.isSenderInWhitelist(publicKeySenderHash, receiverInbox);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void connectRequesterAndReceiver(Inbox inbox, Inbox requesterInbox, String senderPublicKey, String publicKeyToConfirm) {
        Whitelist whitelist = this.findWaitingWhitelistByInboxAndPublicKey(inbox, publicKeyToConfirm);

        whitelist.setState(WhitelistState.APPROVED);
        this.whitelistRepository.save(whitelist);

        createWhiteListEntity(requesterInbox, senderPublicKey, WhitelistState.APPROVED);
        log.info("New public key [{}] was successfully saved into whitelist for inbox [{}]", publicKeyToConfirm, inbox);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void createWhiteListEntity(Inbox inbox, String publicKey, WhitelistState state) {
        Whitelist whitelist = Whitelist.builder()
                .publicKey(publicKey)
                .state(state)
                .inbox(inbox)
                .build();
        this.whitelistRepository.save(whitelist);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void blockPublicKey(Inbox inbox, BlockInboxRequest request) {
        Whitelist whitelist = this.whitelistRepository.findOnWhitelist(inbox, request.publicKeyToBlock())
                .orElseThrow(WhitelistMissingException::new);

        whitelist.setState(request.block() ? WhitelistState.BLOCKED : WhitelistState.APPROVED);
        this.whitelistRepository.save(whitelist);
        log.info("[{}] has been blocked [{}]", whitelist, request.block());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteFromWhiteList(Inbox inbox, String publicKey) {
        this.whitelistRepository.delete(this.findWaitingWhitelistByInboxAndPublicKey(inbox, publicKey));
    }

    private Whitelist findWaitingWhitelistByInboxAndPublicKey(Inbox inbox, String publicKey) {
        return this.whitelistRepository.findByPublicKey(inbox, publicKey, WhitelistState.WAITING)
                .orElseThrow(AlreadyApprovedException::new);
    }
}
