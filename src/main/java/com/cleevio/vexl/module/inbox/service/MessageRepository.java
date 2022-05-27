package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    @Modifying
    @Query("delete from Message m where m.inbox = :inbox and m.pulled = true")
    void deleteAllPulledMessages(Inbox inbox);

    @Query("select case when (count(m) > 0) then true else false end from Message m where m.inbox = :receiverInbox and m.senderPublicKey = :publicKeySender")
    boolean alreadySentRequestAllowence(String publicKeySender, Inbox receiverInbox);

    @Modifying
    @Query("delete from Message m where m.inbox = :inbox")
    void deleteAllMessages(Inbox inbox);
}