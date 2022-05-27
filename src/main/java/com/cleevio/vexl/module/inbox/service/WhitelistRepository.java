package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface WhitelistRepository extends JpaRepository<Whitelist, Long>, JpaSpecificationExecutor<Whitelist> {

    @Query("select case when (count(w) > 0) then true else false end from Whitelist w where w.inbox = :receiverInbox and w.publicKey = :publicKeySenderHash" +
            "  and w.blocked = :blocked")
    boolean isSenderInWhitelist(String publicKeySenderHash, Inbox receiverInbox, boolean blocked);

    @Query("select w from Whitelist w where w.inbox = :receiverInbox and w.publicKey = :publicKeySenderHash")
    Optional<Whitelist> test(String publicKeySenderHash, Inbox receiverInbox);

}