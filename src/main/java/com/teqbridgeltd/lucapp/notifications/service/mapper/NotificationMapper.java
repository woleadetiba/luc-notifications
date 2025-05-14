package com.teqbridgeltd.lucapp.notifications.service.mapper;

import com.teqbridgeltd.lucapp.notifications.domain.Dictionary;
import com.teqbridgeltd.lucapp.notifications.domain.Notification;
import com.teqbridgeltd.lucapp.notifications.service.dto.DictionaryDTO;
import com.teqbridgeltd.lucapp.notifications.service.dto.NotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "status", source = "status", qualifiedByName = "dictionaryId")
    NotificationDTO toDto(Notification s);

    @Named("dictionaryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DictionaryDTO toDtoDictionaryId(Dictionary dictionary);
}
