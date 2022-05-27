package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

interface InboxRepository extends JpaRepository<Inbox, Long> {
    boolean existsByPublicKey(String publicKey);
}