package com.teqbridgeltd.lucapp.notifications.service.mapper;

import com.teqbridgeltd.lucapp.notifications.domain.Notification;
import com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachment;
import com.teqbridgeltd.lucapp.notifications.service.dto.NotificationAttachmentDTO;
import com.teqbridgeltd.lucapp.notifications.service.dto.NotificationDTO;
import java.util.Objects;
import java.util.UUID;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NotificationAttachment} and its DTO {@link NotificationAttachmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationAttachmentMapper extends EntityMapper<NotificationAttachmentDTO, NotificationAttachment> {
    @Mapping(target = "notification", source = "notification", qualifiedByName = "notificationId")
    NotificationAttachmentDTO toDto(NotificationAttachment s);

    @Named("notificationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NotificationDTO toDtoNotificationId(Notification notification);

    default String map(UUID value) {
        return Objects.toString(value, null);
    }
}
