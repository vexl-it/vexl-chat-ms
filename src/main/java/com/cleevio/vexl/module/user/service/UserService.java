package com.cleevio.vexl.module.user.service;

import com.cleevio.vexl.module.user.dto.request.UserRequest;
import com.cleevio.vexl.module.user.exception.GetStreamException;
import io.getstream.chat.java.exceptions.StreamException;
import io.getstream.chat.java.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    public void createUser(UserRequest request)
            throws GetStreamException {
        log.info("Creating user with public key: {}",
                request.getUserPublicKey());

        try {
            User.upsert()
                    .user(
                            User.UserRequestObject.builder()
                                    .id(request.getUserPublicKey())
                                    .name(request.getUsername())
                                    .build()
                    )
                    .request();

        } catch (StreamException e) {
            log.error("Error occurred during user creation", e);
            throw new GetStreamException();
        }
    }

    public void updateUser(UserRequest command)
            throws GetStreamException {
        log.info("Updating user {}",
                command.getUserPublicKey());

        try {
            User.partialUpdate()
                    .user(
                            User.UserPartialUpdateRequestObject.builder()
                                    .id(command.getUserPublicKey())
                                    .setValue("name", command.getUsername())
                                    .build())
                    .request();

        } catch (StreamException e) {
            log.error("Error occurred during user update", e);
            throw new GetStreamException();
        }
    }

    public void deleteUser(String userPublicKey)
            throws GetStreamException {
        log.info("Deleting user {}",
                userPublicKey);

        try {
            User.delete(userPublicKey)
                    .deleteConversationChannels(true)
                    .markMessagesDeleted(true)
                    .hardDelete(true)
                    .request();

        } catch (StreamException e) {
            log.error("Error occurred during user deletion", e);
            throw new GetStreamException();
        }
    }
}
