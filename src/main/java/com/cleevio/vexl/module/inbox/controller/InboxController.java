package com.cleevio.vexl.module.inbox.controller;

import com.cleevio.vexl.module.challenge.exception.InvalidChallengeSignature;
import com.cleevio.vexl.module.challenge.service.ChallengeService;
import com.cleevio.vexl.module.inbox.dto.request.CreateInboxRequest;
import com.cleevio.vexl.module.inbox.dto.request.MessageRequest;
import com.cleevio.vexl.module.inbox.dto.response.MessageResponse;
import com.cleevio.vexl.module.inbox.entity.Inbox;
import com.cleevio.vexl.module.inbox.entity.Message;
import com.cleevio.vexl.module.inbox.mapper.MessageMapper;
import com.cleevio.vexl.module.inbox.service.InboxService;
import com.cleevio.vexl.module.inbox.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Tag(name = "Inbox")
@RestController
@RequestMapping("/api/v1/inboxes")
@RequiredArgsConstructor
public class InboxController {

    private final InboxService inboxService;
    private final ChallengeService challengeService;
    private final MessageService messageService;
    private final MessageMapper messageMapper;

    @PostMapping
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Create a new inbox.", description = "Every user and every offer must have own inbox.")
    ResponseEntity<Void> createInbox(@Valid @RequestBody CreateInboxRequest request) {
        this.inboxService.createInbox(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/messages")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @Operation(summary = "Retrieve messages from my inbox and set them as 'pulled'.", description = """
            Every user and every offer must have own inbox.\040
            Signature in the request params is to verify that the client owns the private key to the public key that he claims is his.\040
            First you need to retrieve challenge for verification in challenge API. Then sign it with private key and the signature send here.
            """)
    Set<MessageResponse> retrieveMessages(@Valid @RequestBody MessageRequest request) {
        if (!this.challengeService.isSignedChallengeValid(request)) {
            throw new InvalidChallengeSignature();
        }

        Inbox inbox = this.inboxService.findInbox(request.publicKey());
        Set<Message> messages = this.messageService.retrieveMessages(inbox);
        return messageMapper.mapSet(messages);
    }

    @DeleteMapping("{publicKey}/messages")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @Operation(summary = "Delete messages what you already have pulled.",
            description = "After every pull, check if you have all messages and then remove them with this EP.")
    void deletePulledMessages(@PathVariable @NotBlank String publicKey) {
        Inbox inbox = this.inboxService.findInbox(publicKey);
        this.messageService.deletePulledMessages(inbox);
    }

}
