package com.teqbridgeltd.lucapp.notifications.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.teqbridgeltd.lucapp.notifications.domain.Notification} entity. This class is used
 * in {@link com.teqbridgeltd.lucapp.notifications.web.rest.NotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private UUIDFilter id;

    private StringFilter recipientEmails;

    private StringFilter ccEmails;

    private StringFilter subject;

    private StringFilter messageBody;

    private IntegerFilter retryCount;

    private IntegerFilter maxRetries;

    private InstantFilter scheduledAt;

    private InstantFilter sentAt;

    private StringFilter errorMessage;

    private InstantFilter createdAt;

    private StringFilter createdBy;

    private LongFilter statusId;

    private UUIDFilter attachmentsId;

    private Boolean distinct;

    public NotificationCriteria() {}

    public NotificationCriteria(NotificationCriteria other) {
        this.id = other.optionalId().map(UUIDFilter::copy).orElse(null);
        this.recipientEmails = other.optionalRecipientEmails().map(StringFilter::copy).orElse(null);
        this.ccEmails = other.optionalCcEmails().map(StringFilter::copy).orElse(null);
        this.subject = other.optionalSubject().map(StringFilter::copy).orElse(null);
        this.messageBody = other.optionalMessageBody().map(StringFilter::copy).orElse(null);
        this.retryCount = other.optionalRetryCount().map(IntegerFilter::copy).orElse(null);
        this.maxRetries = other.optionalMaxRetries().map(IntegerFilter::copy).orElse(null);
        this.scheduledAt = other.optionalScheduledAt().map(InstantFilter::copy).orElse(null);
        this.sentAt = other.optionalSentAt().map(InstantFilter::copy).orElse(null);
        this.errorMessage = other.optionalErrorMessage().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.statusId = other.optionalStatusId().map(LongFilter::copy).orElse(null);
        this.attachmentsId = other.optionalAttachmentsId().map(UUIDFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public NotificationCriteria copy() {
        return new NotificationCriteria(this);
    }

    public UUIDFilter getId() {
        return id;
    }

    public Optional<UUIDFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public UUIDFilter id() {
        if (id == null) {
            setId(new UUIDFilter());
        }
        return id;
    }

    public void setId(UUIDFilter id) {
        this.id = id;
    }

    public StringFilter getRecipientEmails() {
        return recipientEmails;
    }

    public Optional<StringFilter> optionalRecipientEmails() {
        return Optional.ofNullable(recipientEmails);
    }

    public StringFilter recipientEmails() {
        if (recipientEmails == null) {
            setRecipientEmails(new StringFilter());
        }
        return recipientEmails;
    }

    public void setRecipientEmails(StringFilter recipientEmails) {
        this.recipientEmails = recipientEmails;
    }

    public StringFilter getCcEmails() {
        return ccEmails;
    }

    public Optional<StringFilter> optionalCcEmails() {
        return Optional.ofNullable(ccEmails);
    }

    public StringFilter ccEmails() {
        if (ccEmails == null) {
            setCcEmails(new StringFilter());
        }
        return ccEmails;
    }

    public void setCcEmails(StringFilter ccEmails) {
        this.ccEmails = ccEmails;
    }

    public StringFilter getSubject() {
        return subject;
    }

    public Optional<StringFilter> optionalSubject() {
        return Optional.ofNullable(subject);
    }

    public StringFilter subject() {
        if (subject == null) {
            setSubject(new StringFilter());
        }
        return subject;
    }

    public void setSubject(StringFilter subject) {
        this.subject = subject;
    }

    public StringFilter getMessageBody() {
        return messageBody;
    }

    public Optional<StringFilter> optionalMessageBody() {
        return Optional.ofNullable(messageBody);
    }

    public StringFilter messageBody() {
        if (messageBody == null) {
            setMessageBody(new StringFilter());
        }
        return messageBody;
    }

    public void setMessageBody(StringFilter messageBody) {
        this.messageBody = messageBody;
    }

    public IntegerFilter getRetryCount() {
        return retryCount;
    }

    public Optional<IntegerFilter> optionalRetryCount() {
        return Optional.ofNullable(retryCount);
    }

    public IntegerFilter retryCount() {
        if (retryCount == null) {
            setRetryCount(new IntegerFilter());
        }
        return retryCount;
    }

    public void setRetryCount(IntegerFilter retryCount) {
        this.retryCount = retryCount;
    }

    public IntegerFilter getMaxRetries() {
        return maxRetries;
    }

    public Optional<IntegerFilter> optionalMaxRetries() {
        return Optional.ofNullable(maxRetries);
    }

    public IntegerFilter maxRetries() {
        if (maxRetries == null) {
            setMaxRetries(new IntegerFilter());
        }
        return maxRetries;
    }

    public void setMaxRetries(IntegerFilter maxRetries) {
        this.maxRetries = maxRetries;
    }

    public InstantFilter getScheduledAt() {
        return scheduledAt;
    }

    public Optional<InstantFilter> optionalScheduledAt() {
        return Optional.ofNullable(scheduledAt);
    }

    public InstantFilter scheduledAt() {
        if (scheduledAt == null) {
            setScheduledAt(new InstantFilter());
        }
        return scheduledAt;
    }

    public void setScheduledAt(InstantFilter scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public InstantFilter getSentAt() {
        return sentAt;
    }

    public Optional<InstantFilter> optionalSentAt() {
        return Optional.ofNullable(sentAt);
    }

    public InstantFilter sentAt() {
        if (sentAt == null) {
            setSentAt(new InstantFilter());
        }
        return sentAt;
    }

    public void setSentAt(InstantFilter sentAt) {
        this.sentAt = sentAt;
    }

    public StringFilter getErrorMessage() {
        return errorMessage;
    }

    public Optional<StringFilter> optionalErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public StringFilter errorMessage() {
        if (errorMessage == null) {
            setErrorMessage(new StringFilter());
        }
        return errorMessage;
    }

    public void setErrorMessage(StringFilter errorMessage) {
        this.errorMessage = errorMessage;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            setCreatedBy(new StringFilter());
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public LongFilter getStatusId() {
        return statusId;
    }

    public Optional<LongFilter> optionalStatusId() {
        return Optional.ofNullable(statusId);
    }

    public LongFilter statusId() {
        if (statusId == null) {
            setStatusId(new LongFilter());
        }
        return statusId;
    }

    public void setStatusId(LongFilter statusId) {
        this.statusId = statusId;
    }

    public UUIDFilter getAttachmentsId() {
        return attachmentsId;
    }

    public Optional<UUIDFilter> optionalAttachmentsId() {
        return Optional.ofNullable(attachmentsId);
    }

    public UUIDFilter attachmentsId() {
        if (attachmentsId == null) {
            setAttachmentsId(new UUIDFilter());
        }
        return attachmentsId;
    }

    public void setAttachmentsId(UUIDFilter attachmentsId) {
        this.attachmentsId = attachmentsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NotificationCriteria that = (NotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(recipientEmails, that.recipientEmails) &&
            Objects.equals(ccEmails, that.ccEmails) &&
            Objects.equals(subject, that.subject) &&
            Objects.equals(messageBody, that.messageBody) &&
            Objects.equals(retryCount, that.retryCount) &&
            Objects.equals(maxRetries, that.maxRetries) &&
            Objects.equals(scheduledAt, that.scheduledAt) &&
            Objects.equals(sentAt, that.sentAt) &&
            Objects.equals(errorMessage, that.errorMessage) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(statusId, that.statusId) &&
            Objects.equals(attachmentsId, that.attachmentsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            recipientEmails,
            ccEmails,
            subject,
            messageBody,
            retryCount,
            maxRetries,
            scheduledAt,
            sentAt,
            errorMessage,
            createdAt,
            createdBy,
            statusId,
            attachmentsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRecipientEmails().map(f -> "recipientEmails=" + f + ", ").orElse("") +
            optionalCcEmails().map(f -> "ccEmails=" + f + ", ").orElse("") +
            optionalSubject().map(f -> "subject=" + f + ", ").orElse("") +
            optionalMessageBody().map(f -> "messageBody=" + f + ", ").orElse("") +
            optionalRetryCount().map(f -> "retryCount=" + f + ", ").orElse("") +
            optionalMaxRetries().map(f -> "maxRetries=" + f + ", ").orElse("") +
            optionalScheduledAt().map(f -> "scheduledAt=" + f + ", ").orElse("") +
            optionalSentAt().map(f -> "sentAt=" + f + ", ").orElse("") +
            optionalErrorMessage().map(f -> "errorMessage=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalStatusId().map(f -> "statusId=" + f + ", ").orElse("") +
            optionalAttachmentsId().map(f -> "attachmentsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
