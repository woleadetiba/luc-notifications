package com.teqbridgeltd.lucapp.notifications.domain;

import static com.teqbridgeltd.lucapp.notifications.domain.DictionaryTestSamples.*;
import static com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachmentTestSamples.*;
import static com.teqbridgeltd.lucapp.notifications.domain.NotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.teqbridgeltd.lucapp.notifications.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void statusTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Dictionary dictionaryBack = getDictionaryRandomSampleGenerator();

        notification.setStatus(dictionaryBack);
        assertThat(notification.getStatus()).isEqualTo(dictionaryBack);

        notification.status(null);
        assertThat(notification.getStatus()).isNull();
    }

    @Test
    void attachmentsTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        NotificationAttachment notificationAttachmentBack = getNotificationAttachmentRandomSampleGenerator();

        notification.addAttachments(notificationAttachmentBack);
        assertThat(notification.getAttachments()).containsOnly(notificationAttachmentBack);
        assertThat(notificationAttachmentBack.getNotification()).isEqualTo(notification);

        notification.removeAttachments(notificationAttachmentBack);
        assertThat(notification.getAttachments()).doesNotContain(notificationAttachmentBack);
        assertThat(notificationAttachmentBack.getNotification()).isNull();

        notification.attachments(new HashSet<>(Set.of(notificationAttachmentBack)));
        assertThat(notification.getAttachments()).containsOnly(notificationAttachmentBack);
        assertThat(notificationAttachmentBack.getNotification()).isEqualTo(notification);

        notification.setAttachments(new HashSet<>());
        assertThat(notification.getAttachments()).doesNotContain(notificationAttachmentBack);
        assertThat(notificationAttachmentBack.getNotification()).isNull();
    }
}
