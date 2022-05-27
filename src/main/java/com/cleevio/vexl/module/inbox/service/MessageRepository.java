package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.module.inbox.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

interface MessageRepository extends JpaRepository<Message, Long> {

}