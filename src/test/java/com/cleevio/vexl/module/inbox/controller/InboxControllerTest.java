package com.cleevio.vexl.module.inbox.controller;

import com.cleevio.vexl.common.BaseControllerTest;
import com.cleevio.vexl.common.security.filter.SecurityFilter;
import com.cleevio.vexl.module.inbox.dto.request.BlockInboxRequest;
import com.cleevio.vexl.module.inbox.dto.request.MessageRequest;
import com.cleevio.vexl.module.inbox.dto.request.UpdateInboxRequest;
import com.cleevio.vexl.module.inbox.dto.response.MessagesResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(InboxController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InboxControllerTest extends BaseControllerTest {

    private static final String DEFAULT_EP = "/api/v1/inboxes";
    private static final String MESSAGES_EP = DEFAULT_EP + "/messages";
    private static final String BLOCK_EP = DEFAULT_EP + "/block";
    private static final String POST_MESSAGE_EP = DEFAULT_EP + "/messages";
    private static final String APPROVAL_REQUEST = DEFAULT_EP + "/approval/request";
    private static final String APPROVAL_CONFIRM = DEFAULT_EP + "/approval/confirm";
    private static final String DELETE_MESSAGES = DEFAULT_EP + "/messages";
    private static final UpdateInboxRequest UPDATE_INBOX_REQUEST;
    private static final MessageRequest MESSAGE_REQUEST;
    private static final BlockInboxRequest BLOCK_INBOX_REQUEST;

    private static final String CREATE_INBOX = """
               {
                "publicKey": "dummy_public_key",
                "token": "123"
                }
            """;

    private static final String POST_MESSAGE = String.format("""
            {
                "senderPublicKey": "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUU0d0VBWUhLb1pJemowQ0FRWUZLNEVFQUNFRE9nQUVqT2xDSnhwVHFFZ1k2T0FER2lTdXdUbjBJZWFIZHZEawo0NkZYeDM5Yk5memY0Ry9zcFZXb1NibTIvODVhbmNodDE1c2hzSmdONnVBPQotLS0tLUVORCBQVUJMSUMgS0VZLS0tLS0K",
                "receiverPublicKey": "%s",
                "message": "last",
                "messageType": "MESSAGE"
            }
                        """, INBOX_PUBLIC_KEY);

    private static final String APPROVAL_REQUEST_PAYLOAD = String.format("""
            {
                "publicKey": "%s",
                "message": "dummy_message"
            }
                                    """, INBOX_PUBLIC_KEY);

    private static final String DELETE_PAYLOAD = String.format("""
            {
                "publicKey": "%s"
            }
                                    """, INBOX_PUBLIC_KEY);

    private static final String APPROVAL_CONFIRM_PAYLOAD = String.format("""
            {
                "publicKey": "%s",
                "publicKeyToConfirm": "dummy_pk_to_confirm",
                "signature": "%s",
                "message": "Yes, I approve. Let's meet in the most beautiful part of Prague, in Karlin.",
                "approve": true
            }
                                                """, INBOX_PUBLIC_KEY, CHALLENGE_SIGNATURE);

    static {
        UPDATE_INBOX_REQUEST = new UpdateInboxRequest(INBOX_PUBLIC_KEY, FIREBASE_TOKEN);

        MESSAGE_REQUEST = new MessageRequest(INBOX_PUBLIC_KEY, CHALLENGE_SIGNATURE);

        BLOCK_INBOX_REQUEST = new BlockInboxRequest(INBOX_PUBLIC_KEY, PUBLIC_KEY_HEADER, CHALLENGE_SIGNATURE, true);
    }

    @Test
    @SneakyThrows
    void testCreateInbox_validInput_shouldReturn204() {
        mvc.perform(post(DEFAULT_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_INBOX))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void testCreateInbox_invalidInput_shouldReturn400() {
        mvc.perform(post(DEFAULT_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_INBOX.replace("publicKey", "dummy")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void testUpdateInbox_validInput_shouldReturn202() {
        when(inboxService.updateInbox(INBOX, FIREBASE_TOKEN)).thenReturn(INBOX);
        when(inboxService.findInbox(INBOX_PUBLIC_KEY)).thenReturn(INBOX);

        mvc.perform(put(DEFAULT_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(UPDATE_INBOX_REQUEST)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.firebaseToken", is(FIREBASE_TOKEN)));
    }

    @Test
    @SneakyThrows
    void testRetrieveMessages_validInput_shouldReturn200() {
        final var messages = List.of(MESSAGE);
        final var messageResponse = new MessagesResponse.MessageResponse(MESSAGE.getMessage(), MESSAGE.getSenderPublicKey(), MESSAGE.getType());
        when(challengeService.isSignedChallengeValid(INBOX_PUBLIC_KEY, CHALLENGE_SIGNATURE)).thenReturn(true);
        when(inboxService.findInbox(INBOX_PUBLIC_KEY)).thenReturn(INBOX);
        when(messageService.retrieveMessages(INBOX)).thenReturn(messages);
        when(messageMapper.mapList(messages)).thenReturn(List.of(messageResponse));

        mvc.perform(put(MESSAGES_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(MESSAGE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].message", is(MESSAGE.getMessage())))
                .andExpect(jsonPath("$.messages[0].senderPublicKey", is(MESSAGE.getSenderPublicKey())))
                .andExpect(jsonPath("$.messages[0].messageType", is(MESSAGE.getType().name())));
    }

    @Test
    @SneakyThrows
    void testBlock_validInput_shouldReturn204() {
        when(challengeService.isSignedChallengeValid(INBOX_PUBLIC_KEY, CHALLENGE_SIGNATURE)).thenReturn(true);
        when(inboxService.findInbox(INBOX_PUBLIC_KEY)).thenReturn(INBOX);

        mvc.perform(put(BLOCK_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(BLOCK_INBOX_REQUEST)))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void testPostMessage_validInput_shouldReturn204() {
        when(inboxService.findInbox(INBOX_PUBLIC_KEY)).thenReturn(INBOX);

        mvc.perform(post(POST_MESSAGE_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(POST_MESSAGE))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void testApprovalRequest_validInput_shouldReturn204() {
        when(inboxService.findInbox(INBOX_PUBLIC_KEY)).thenReturn(INBOX);

        mvc.perform(post(APPROVAL_REQUEST)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(APPROVAL_REQUEST_PAYLOAD))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void testApprovalConfirm_validInput_shouldReturn204() {
        when(challengeService.isSignedChallengeValid(INBOX_PUBLIC_KEY, CHALLENGE_SIGNATURE)).thenReturn(true);
        when(inboxService.findInbox(any())).thenReturn(INBOX);

        mvc.perform(post(APPROVAL_CONFIRM)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(APPROVAL_CONFIRM_PAYLOAD))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void testDeleteMessages_validInput_shouldReturn200() {
        when(inboxService.findInbox(any())).thenReturn(INBOX);

        mvc.perform(delete(DELETE_MESSAGES)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DELETE_PAYLOAD))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void testDeleteInboxAndMessages_validInput_shouldReturn200() {
        when(inboxService.findInbox(any())).thenReturn(INBOX);

        mvc.perform(delete(DEFAULT_EP)
                        .header(SecurityFilter.HEADER_PUBLIC_KEY, PUBLIC_KEY_HEADER)
                        .header(SecurityFilter.HEADER_HASH, HASH_HEADER)
                        .header(SecurityFilter.HEADER_SIGNATURE, SIGNATURE_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DELETE_PAYLOAD))
                .andExpect(status().isOk());
    }
}
