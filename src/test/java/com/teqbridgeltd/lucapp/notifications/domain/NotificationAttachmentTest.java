package com.teqbridgeltd.lucapp.notifications.domain;

import static com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachmentTestSamples.*;
import static com.teqbridgeltd.lucapp.notifications.domain.NotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.teqbridgeltd.lucapp.notifications.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationAttachmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationAttachment.class);
        NotificationAttachment notificationAttachment1 = getNotificationAttachmentSample1();
        NotificationAttachment notificationAttachment2 = new NotificationAttachment();
        assertThat(notificationAttachment1).isNotEqualTo(notificationAttachment2);

        notificationAttachment2.setId(notificationAttachment1.getId());
        assertThat(notificationAttachment1).isEqualTo(notificationAttachment2);

        notificationAttachment2 = getNotificationAttachmentSample2();
        assertThat(notificationAttachment1).isNotEqualTo(notificationAttachment2);
    }

    @Test
    void notificationTest() {
        NotificationAttachment notificationAttachment = getNotificationAttachmentRandomSampleGenerator();
        Notification notificationBack = getNotificationRandomSampleGenerator();

        notificationAttachment.setNotification(notificationBack);
        assertThat(notificationAttachment.getNotification()).isEqualTo(notificationBack);

        notificationAttachment.notification(null);
        assertThat(notificationAttachment.getNotification()).isNull();
    }
}
