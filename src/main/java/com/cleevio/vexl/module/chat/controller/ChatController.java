package com.cleevio.vexl.module.chat.controller;

import com.cleevio.vexl.common.dto.ErrorResponse;
import com.cleevio.vexl.module.chat.dto.request.MessageRequest;
import com.cleevio.vexl.module.chat.dto.response.MessagesResponse;
import com.cleevio.vexl.module.chat.exception.ChannelException;
import com.cleevio.vexl.module.chat.exception.GettingMessagesException;
import com.cleevio.vexl.module.chat.exception.SendingMessageException;
import com.cleevio.vexl.module.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Chat")
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "406 (101100)", description = "Channel couldn't be created or found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406 (101101)", description = "Error occurred while sending message", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Operation(summary = "Create an user in GetStream service (as an unique identifier we user public_key)")
    ResponseEntity<Void> sendMessage(@Valid @RequestBody MessageRequest request)
            throws ChannelException, SendingMessageException {
        this.chatService.sendMessage(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{channelId}/")
    @SecurityRequirements({
            @SecurityRequirement(name = "public-key"),
            @SecurityRequirement(name = "phone-hash"),
            @SecurityRequirement(name = "signature"),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400 (101102)", description = "Cannot receive messages from channel", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Operation(summary = "Get all messages from channel")
    MessagesResponse retrieveMessagesFromChannel(@PathVariable String channelId)
            throws GettingMessagesException {
        return new MessagesResponse(this.chatService.receiveMessageFromChannel(channelId));
    }
}
