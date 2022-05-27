package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface InboxRepository extends JpaRepository<Inbox, Long> {
    boolean existsByPublicKey(String publicKey);

    Optional<Inbox> findByPublicKey(String publicKey);
}