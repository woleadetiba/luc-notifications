package com.teqbridgeltd.lucapp.notifications.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationAttachmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static NotificationAttachment getNotificationAttachmentSample1() {
        return new NotificationAttachment()
            .id(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .fileName("fileName1")
            .fileType("fileType1")
            .fileSize(1L)
            .filePath("filePath1");
    }

    public static NotificationAttachment getNotificationAttachmentSample2() {
        return new NotificationAttachment()
            .id(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .fileName("fileName2")
            .fileType("fileType2")
            .fileSize(2L)
            .filePath("filePath2");
    }

    public static NotificationAttachment getNotificationAttachmentRandomSampleGenerator() {
        return new NotificationAttachment()
            .id(UUID.randomUUID())
            .fileName(UUID.randomUUID().toString())
            .fileType(UUID.randomUUID().toString())
            .fileSize(longCount.incrementAndGet())
            .filePath(UUID.randomUUID().toString());
    }
}
