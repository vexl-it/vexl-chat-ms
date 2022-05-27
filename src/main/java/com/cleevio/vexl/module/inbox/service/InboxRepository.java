package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

interface InboxRepository extends JpaRepository<Inbox, Long>, JpaSpecificationExecutor<Inbox> {
    boolean existsByPublicKey(String publicKey);

    Optional<Inbox> findByPublicKey(String publicKey);
}