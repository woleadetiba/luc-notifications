package com.teqbridgeltd.lucapp.notifications.repository;

import com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachment;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the NotificationAttachment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationAttachmentRepository
    extends JpaRepository<NotificationAttachment, UUID>, JpaSpecificationExecutor<NotificationAttachment> {}
