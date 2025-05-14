package com.teqbridgeltd.lucapp.notifications.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.teqbridgeltd.lucapp.notifications.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NotificationAttachmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationAttachmentDTO.class);
        NotificationAttachmentDTO notificationAttachmentDTO1 = new NotificationAttachmentDTO();
        notificationAttachmentDTO1.setId(UUID.randomUUID());
        NotificationAttachmentDTO notificationAttachmentDTO2 = new NotificationAttachmentDTO();
        assertThat(notificationAttachmentDTO1).isNotEqualTo(notificationAttachmentDTO2);
        notificationAttachmentDTO2.setId(notificationAttachmentDTO1.getId());
        assertThat(notificationAttachmentDTO1).isEqualTo(notificationAttachmentDTO2);
        notificationAttachmentDTO2.setId(UUID.randomUUID());
        assertThat(notificationAttachmentDTO1).isNotEqualTo(notificationAttachmentDTO2);
        notificationAttachmentDTO1.setId(null);
        assertThat(notificationAttachmentDTO1).isNotEqualTo(notificationAttachmentDTO2);
    }
}
