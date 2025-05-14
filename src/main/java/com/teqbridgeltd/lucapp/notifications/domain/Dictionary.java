package com.teqbridgeltd.lucapp.notifications.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Dictionary.
 */
@Entity
@Table(name = "dictionary")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "dictionary")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Dictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "key_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String keyName;

    @Column(name = "key_code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String keyCode;

    @Column(name = "label")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String label;

    @Column(name = "description")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @JsonIgnoreProperties(value = { "status", "attachments" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "status")
    @org.springframework.data.annotation.Transient
    private Notification notification;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Dictionary id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyName() {
        return this.keyName;
    }

    public Dictionary keyName(String keyName) {
        this.setKeyName(keyName);
        return this;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyCode() {
        return this.keyCode;
    }

    public Dictionary keyCode(String keyCode) {
        this.setKeyCode(keyCode);
        return this;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }

    public String getLabel() {
        return this.label;
    }

    public Dictionary label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return this.description;
    }

    public Dictionary description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        if (this.notification != null) {
            this.notification.setStatus(null);
        }
        if (notification != null) {
            notification.setStatus(this);
        }
        this.notification = notification;
    }

    public Dictionary notification(Notification notification) {
        this.setNotification(notification);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dictionary)) {
            return false;
        }
        return getId() != null && getId().equals(((Dictionary) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Dictionary{" +
            "id=" + getId() +
            ", keyName='" + getKeyName() + "'" +
            ", keyCode='" + getKeyCode() + "'" +
            ", label='" + getLabel() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
