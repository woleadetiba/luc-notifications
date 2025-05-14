package com.teqbridgeltd.lucapp.notifications.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachment} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationAttachmentDTO implements Serializable {

    private UUID id;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String filePath;

    private Instant createdAt;

    private NotificationDTO notification;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public NotificationDTO getNotification() {
        return notification;
    }

    public void setNotification(NotificationDTO notification) {
        this.notification = notification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationAttachmentDTO)) {
            return false;
        }

        NotificationAttachmentDTO notificationAttachmentDTO = (NotificationAttachmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationAttachmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationAttachmentDTO{" +
            "id='" + getId() + "'" +
            ", fileName='" + getFileName() + "'" +
            ", fileType='" + getFileType() + "'" +
            ", fileSize=" + getFileSize() +
            ", filePath='" + getFilePath() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", notification=" + getNotification() +
            "}";
    }
}
