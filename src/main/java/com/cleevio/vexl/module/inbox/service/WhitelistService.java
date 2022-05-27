package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Whitelist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.cleevio.vexl.common.service.CryptoService.createHash256;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhitelistService {

    private final WhitelistRepository whitelistRepository;

    public boolean isSenderInWhitelistNotBlocked(String publicKeySenderHash, Inbox receiverInbox) {
        return this.whitelistRepository.isSenderInWhitelist(publicKeySenderHash, receiverInbox, false);
    }

    public boolean isSenderInWhitelistBlocked(String publicKeySenderHash, Inbox receiverInbox) {
        return this.whitelistRepository.isSenderInWhitelist(publicKeySenderHash, receiverInbox, true);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void putSenderPublicKeyOnWhitelist(Inbox inbox, String publicKeyToConfirm) {
        String publicKeyHash = createHash256(publicKeyToConfirm);
        Whitelist whitelist = createWhiteListEntity(inbox, publicKeyHash);
        this.whitelistRepository.save(whitelist);
        log.info("New public key [{}] was successfully saved into whitelist for inbox [{}]", publicKeyHash, inbox);
    }

    private Whitelist createWhiteListEntity(Inbox inbox, String publicKeyHash) {
        return Whitelist.builder()
                .publicKey(publicKeyHash)
                .blocked(false)
                .inbox(inbox)
                .build();
    }
}
