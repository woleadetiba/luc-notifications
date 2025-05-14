package com.teqbridgeltd.lucapp.notifications.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.teqbridgeltd.lucapp.notifications.domain.Dictionary} entity. This class is used
 * in {@link com.teqbridgeltd.lucapp.notifications.web.rest.DictionaryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /dictionaries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DictionaryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter keyName;

    private StringFilter keyCode;

    private StringFilter label;

    private StringFilter description;

    private UUIDFilter notificationId;

    private Boolean distinct;

    public DictionaryCriteria() {}

    public DictionaryCriteria(DictionaryCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.keyName = other.optionalKeyName().map(StringFilter::copy).orElse(null);
        this.keyCode = other.optionalKeyCode().map(StringFilter::copy).orElse(null);
        this.label = other.optionalLabel().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.notificationId = other.optionalNotificationId().map(UUIDFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DictionaryCriteria copy() {
        return new DictionaryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getKeyName() {
        return keyName;
    }

    public Optional<StringFilter> optionalKeyName() {
        return Optional.ofNullable(keyName);
    }

    public StringFilter keyName() {
        if (keyName == null) {
            setKeyName(new StringFilter());
        }
        return keyName;
    }

    public void setKeyName(StringFilter keyName) {
        this.keyName = keyName;
    }

    public StringFilter getKeyCode() {
        return keyCode;
    }

    public Optional<StringFilter> optionalKeyCode() {
        return Optional.ofNullable(keyCode);
    }

    public StringFilter keyCode() {
        if (keyCode == null) {
            setKeyCode(new StringFilter());
        }
        return keyCode;
    }

    public void setKeyCode(StringFilter keyCode) {
        this.keyCode = keyCode;
    }

    public StringFilter getLabel() {
        return label;
    }

    public Optional<StringFilter> optionalLabel() {
        return Optional.ofNullable(label);
    }

    public StringFilter label() {
        if (label == null) {
            setLabel(new StringFilter());
        }
        return label;
    }

    public void setLabel(StringFilter label) {
        this.label = label;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public UUIDFilter getNotificationId() {
        return notificationId;
    }

    public Optional<UUIDFilter> optionalNotificationId() {
        return Optional.ofNullable(notificationId);
    }

    public UUIDFilter notificationId() {
        if (notificationId == null) {
            setNotificationId(new UUIDFilter());
        }
        return notificationId;
    }

    public void setNotificationId(UUIDFilter notificationId) {
        this.notificationId = notificationId;
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
        final DictionaryCriteria that = (DictionaryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(keyName, that.keyName) &&
            Objects.equals(keyCode, that.keyCode) &&
            Objects.equals(label, that.label) &&
            Objects.equals(description, that.description) &&
            Objects.equals(notificationId, that.notificationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keyName, keyCode, label, description, notificationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DictionaryCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalKeyName().map(f -> "keyName=" + f + ", ").orElse("") +
            optionalKeyCode().map(f -> "keyCode=" + f + ", ").orElse("") +
            optionalLabel().map(f -> "label=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalNotificationId().map(f -> "notificationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
