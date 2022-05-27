package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

interface MessageRepository extends JpaRepository<Message, Long> {

    @Modifying
    @Query("delete from Message m where m.inbox = :inbox and m.pulled = true")
    void deleteAllPulledMessages(Inbox inbox);
}