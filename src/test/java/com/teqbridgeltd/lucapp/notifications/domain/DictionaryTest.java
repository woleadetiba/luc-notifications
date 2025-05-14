package com.teqbridgeltd.lucapp.notifications.domain;

import static com.teqbridgeltd.lucapp.notifications.domain.DictionaryTestSamples.*;
import static com.teqbridgeltd.lucapp.notifications.domain.NotificationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.teqbridgeltd.lucapp.notifications.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DictionaryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dictionary.class);
        Dictionary dictionary1 = getDictionarySample1();
        Dictionary dictionary2 = new Dictionary();
        assertThat(dictionary1).isNotEqualTo(dictionary2);

        dictionary2.setId(dictionary1.getId());
        assertThat(dictionary1).isEqualTo(dictionary2);

        dictionary2 = getDictionarySample2();
        assertThat(dictionary1).isNotEqualTo(dictionary2);
    }

    @Test
    void notificationTest() {
        Dictionary dictionary = getDictionaryRandomSampleGenerator();
        Notification notificationBack = getNotificationRandomSampleGenerator();

        dictionary.setNotification(notificationBack);
        assertThat(dictionary.getNotification()).isEqualTo(notificationBack);
        assertThat(notificationBack.getStatus()).isEqualTo(dictionary);

        dictionary.notification(null);
        assertThat(dictionary.getNotification()).isNull();
        assertThat(notificationBack.getStatus()).isNull();
    }
}
