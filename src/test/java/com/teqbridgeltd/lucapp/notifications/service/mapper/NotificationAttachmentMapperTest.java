package com.teqbridgeltd.lucapp.notifications.service.mapper;

import static com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachmentAsserts.*;
import static com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationAttachmentMapperTest {

    private NotificationAttachmentMapper notificationAttachmentMapper;

    @BeforeEach
    void setUp() {
        notificationAttachmentMapper = new NotificationAttachmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotificationAttachmentSample1();
        var actual = notificationAttachmentMapper.toEntity(notificationAttachmentMapper.toDto(expected));
        assertNotificationAttachmentAllPropertiesEquals(expected, actual);
    }
}
