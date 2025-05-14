package com.teqbridgeltd.lucapp.notifications.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Notification getNotificationSample1() {
        return new Notification()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .recipientEmails("recipientEmails1")
            .ccEmails("ccEmails1")
            .subject("subject1")
            .messageBody("messageBody1")
            .retryCount(1)
            .maxRetries(1)
            .errorMessage("errorMessage1")
            .createdBy("createdBy1");
    }

    public static Notification getNotificationSample2() {
        return new Notification()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .recipientEmails("recipientEmails2")
            .ccEmails("ccEmails2")
            .subject("subject2")
            .messageBody("messageBody2")
            .retryCount(2)
            .maxRetries(2)
            .errorMessage("errorMessage2")
            .createdBy("createdBy2");
    }

    public static Notification getNotificationRandomSampleGenerator() {
        return new Notification()
            .id(UUID.randomUUID())
            .recipientEmails(UUID.randomUUID().toString())
            .ccEmails(UUID.randomUUID().toString())
            .subject(UUID.randomUUID().toString())
            .messageBody(UUID.randomUUID().toString())
            .retryCount(intCount.incrementAndGet())
            .maxRetries(intCount.incrementAndGet())
            .errorMessage(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString());
    }
}
