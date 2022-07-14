package com.cleevio.vexl.module.inbox.service;

import com.cleevio.vexl.common.IntegrationTest;
import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Message;
import com.cleevio.vexl.module.inbox.entity.Whitelist;
import com.cleevio.vexl.module.inbox.enums.MessageType;
import com.cleevio.vexl.module.inbox.enums.WhitelistState;
import com.cleevio.vexl.module.inbox.exception.AlreadyApprovedException;
import com.cleevio.vexl.module.inbox.exception.DuplicatedPublicKeyException;
import com.cleevio.vexl.module.inbox.exception.RequestMessagingNotAllowedException;
import com.cleevio.vexl.utils.CryptographyTestKeysUtil;
import com.cleevio.vexl.utils.RequestCreatorTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InboxMessagingIT {

    private final InboxService inboxService;
    private final WhitelistService whitelistService;
    private final MessageService messageService;
    private final InboxRepository inboxRepository;
    private final WhitelistRepository whitelistRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public InboxMessagingIT(InboxService inboxService, WhitelistService whitelistService,
                            MessageService messageService, InboxRepository inboxRepository,
                            WhitelistRepository whitelistRepository, MessageRepository messageRepository) {
        this.inboxService = inboxService;
        this.whitelistService = whitelistService;
        this.messageService = messageService;
        this.inboxRepository = inboxRepository;
        this.whitelistRepository = whitelistRepository;
        this.messageRepository = messageRepository;
    }

    private static final String REQUEST_APPROVAL_MESSAGE = "dummy_approval_request";
    private static final String CONFIRMATION_MESSAGE = "dummy_confirmation";

    private static final String PUBLIC_KEY_USER_A = CryptographyTestKeysUtil.PUBLIC_KEY_USER_A;
    private static final String PUBLIC_KEY_USER_B = CryptographyTestKeysUtil.PUBLIC_KEY_USER_B;


    @Test
    void testCreateNewInboxes_shouldBeCreated() {
        final var requestUserA = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_A);
        final var requestUserB = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_B);

        this.inboxService.createInbox(requestUserA);
        this.inboxService.createInbox(requestUserB);

        final Inbox inboxA = this.inboxRepository.findByPublicKey(PUBLIC_KEY_USER_A).get();
        final Inbox inboxB = this.inboxRepository.findByPublicKey(PUBLIC_KEY_USER_B).get();

        assertThat(inboxA.getPublicKey()).isEqualTo(PUBLIC_KEY_USER_A);
        assertThat(inboxA.getToken()).isNull();
        assertThat(inboxB.getPublicKey()).isEqualTo(PUBLIC_KEY_USER_B);
        assertThat(inboxB.getToken()).isNull();
    }

    @Test
    void testCreateDuplicatedInbox_shouldThrowException() {
        final var requestUserA = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_A);
        this.inboxService.createInbox(requestUserA);
        assertThat(this.inboxRepository.findAll()).hasSize(1);

        assertThrows(
                DuplicatedPublicKeyException.class,
                () -> this.inboxService.createInbox(requestUserA)
        );
    }

    @Test
    void testCreateApprovalRequest_shouldCreateApprovalRequest() {
        final var requestUserA = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_A);
        final var requestUserB = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_B);

        //creating new inboxes
        this.inboxService.createInbox(requestUserA);
        this.inboxService.createInbox(requestUserB);

        final Inbox inboxToWhichRequestIsSent = this.inboxRepository.findByPublicKey(PUBLIC_KEY_USER_B).get();

        //sending approval request
        this.messageService.sendRequestToPermission(PUBLIC_KEY_USER_A, PUBLIC_KEY_USER_B, inboxToWhichRequestIsSent, REQUEST_APPROVAL_MESSAGE);

        List<Message> messages = this.messageRepository.findAll();

        //verifying message
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getMessage()).isEqualTo(REQUEST_APPROVAL_MESSAGE);
        assertThat(messages.get(0).isPulled()).isEqualTo(false);
        assertThat(messages.get(0).getSenderPublicKey()).isEqualTo(PUBLIC_KEY_USER_A);
        assertThat(messages.get(0).getType()).isEqualTo(MessageType.REQUEST_MESSAGING);

        //verifying record on whitelist
        List<Whitelist> whitelists = this.whitelistRepository.findAll();
        assertThat(whitelists).hasSize(1);
        whitelists.forEach(w -> {
            assertThat(w.getPublicKey()).isEqualTo(PUBLIC_KEY_USER_A);
            assertThat(w.getState()).isEqualTo(WhitelistState.WAITING);
            assertThat(w.getInbox()).isEqualTo(inboxToWhichRequestIsSent);
        });
    }

    @Test
    void testSendDuplicatedApprovalRequest_shouldReturnException() {
        final var requestUserA = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_A);
        final var requestUserB = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_B);

        //creating new inboxes
        this.inboxService.createInbox(requestUserA);
        this.inboxService.createInbox(requestUserB);

        final Inbox inboxToWhichRequestIsSent = this.inboxRepository.findByPublicKey(PUBLIC_KEY_USER_B).get();

        //sending approval request
        this.messageService.sendRequestToPermission(PUBLIC_KEY_USER_A, PUBLIC_KEY_USER_B, inboxToWhichRequestIsSent, REQUEST_APPROVAL_MESSAGE);

        assertThrows(
                RequestMessagingNotAllowedException.class,
                () -> messageService.sendRequestToPermission(PUBLIC_KEY_USER_A, PUBLIC_KEY_USER_B, inboxToWhichRequestIsSent, REQUEST_APPROVAL_MESSAGE)
        );
    }

    @Test
    void testConfirmApprovalRequest_shouldConfirmAndCreateConfirmationMessage() {
        final var requestUserA = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_A);
        final var requestUserB = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_B);

        //creating new inboxes
        this.inboxService.createInbox(requestUserA);
        this.inboxService.createInbox(requestUserB);

        final Inbox confirmer = this.inboxRepository.findByPublicKey(PUBLIC_KEY_USER_A).get();
        final Inbox requester = this.inboxRepository.findByPublicKey(PUBLIC_KEY_USER_B).get();

        //sending approval request
        this.messageService.sendRequestToPermission(PUBLIC_KEY_USER_B, PUBLIC_KEY_USER_A, confirmer, REQUEST_APPROVAL_MESSAGE);

        //get sender public key - sender = requester in this case
        List<Message> messages = this.messageRepository.findAll();
        String publicKeyToConfirm = messages.get(0).getSenderPublicKey();

        this.whitelistService.connectRequesterAndReceiver(confirmer, requester, PUBLIC_KEY_USER_A, publicKeyToConfirm);
        this.messageService.sendMessageToInbox(confirmer.getPublicKey(), PUBLIC_KEY_USER_B, requester, CONFIRMATION_MESSAGE, MessageType.APPROVE_MESSAGING);

        List<Whitelist> whitelistAll = this.whitelistRepository.findAll().stream()
                .sorted(Comparator.comparing(Whitelist::getId))
                .collect(Collectors.toList());

        List<Message> messagesAll = this.messageRepository.findAll().stream()
                .sorted(Comparator.comparing(Message::getId))
                .collect(Collectors.toList());

        assertThat(whitelistAll).hasSize(2);
        assertThat(messagesAll).hasSize(2);

        assertThat(messagesAll.get(0).getMessage()).isEqualTo(REQUEST_APPROVAL_MESSAGE);
        assertThat(messagesAll.get(0).getSenderPublicKey()).isEqualTo(publicKeyToConfirm);
        assertThat(messagesAll.get(1).getMessage()).isEqualTo(CONFIRMATION_MESSAGE);
        assertThat(messagesAll.get(1).getType()).isEqualTo(MessageType.APPROVE_MESSAGING);
        assertThat(messagesAll.get(1).getSenderPublicKey()).isEqualTo(PUBLIC_KEY_USER_A);

        assertThat(whitelistAll.get(0).getPublicKey()).isEqualTo(PUBLIC_KEY_USER_B);
        assertThat(whitelistAll.get(0).getState()).isEqualTo(WhitelistState.APPROVED);
        assertThat(whitelistAll.get(1).getPublicKey()).isEqualTo(PUBLIC_KEY_USER_A);
        assertThat(whitelistAll.get(1).getState()).isEqualTo(WhitelistState.APPROVED);
    }

    @Test
    void testConfirmApprovalRequestTwice_shouldReturnException() {
        final var requestUserA = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_A);
        final var requestUserB = RequestCreatorTestUtil.createInboxRequest(PUBLIC_KEY_USER_B);

        //creating new inboxes
        this.inboxService.createInbox(requestUserA);
        this.inboxService.createInbox(requestUserB);

        final Inbox confirmer = this.inboxRepository.findByPublicKey(PUBLIC_KEY_USER_A).get();
        final Inbox requester = this.inboxRepository.findByPublicKey(PUBLIC_KEY_USER_B).get();

        //sending approval request
        this.messageService.sendRequestToPermission(PUBLIC_KEY_USER_B, PUBLIC_KEY_USER_A, confirmer, REQUEST_APPROVAL_MESSAGE);

        //get sender public key - sender = requester in this case
        List<Message> messages = this.messageRepository.findAll();
        String publicKeyToConfirm = messages.get(0).getSenderPublicKey();

        this.whitelistService.connectRequesterAndReceiver(confirmer, requester, PUBLIC_KEY_USER_A, publicKeyToConfirm);
        this.messageService.sendMessageToInbox(confirmer.getPublicKey(), PUBLIC_KEY_USER_B, requester, CONFIRMATION_MESSAGE, MessageType.APPROVE_MESSAGING);

        assertThrows(
                AlreadyApprovedException.class,
                () -> this.whitelistService.connectRequesterAndReceiver(confirmer, requester, PUBLIC_KEY_USER_A, publicKeyToConfirm)
        );
    }
}
