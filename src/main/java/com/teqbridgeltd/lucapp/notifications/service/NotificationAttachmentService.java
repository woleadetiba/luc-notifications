package com.teqbridgeltd.lucapp.notifications.service;

import com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachment;
import com.teqbridgeltd.lucapp.notifications.repository.NotificationAttachmentRepository;
import com.teqbridgeltd.lucapp.notifications.repository.search.NotificationAttachmentSearchRepository;
import com.teqbridgeltd.lucapp.notifications.service.dto.NotificationAttachmentDTO;
import com.teqbridgeltd.lucapp.notifications.service.mapper.NotificationAttachmentMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachment}.
 */
@Service
@Transactional
public class NotificationAttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationAttachmentService.class);

    private final NotificationAttachmentRepository notificationAttachmentRepository;

    private final NotificationAttachmentMapper notificationAttachmentMapper;

    private final NotificationAttachmentSearchRepository notificationAttachmentSearchRepository;

    public NotificationAttachmentService(
        NotificationAttachmentRepository notificationAttachmentRepository,
        NotificationAttachmentMapper notificationAttachmentMapper,
        NotificationAttachmentSearchRepository notificationAttachmentSearchRepository
    ) {
        this.notificationAttachmentRepository = notificationAttachmentRepository;
        this.notificationAttachmentMapper = notificationAttachmentMapper;
        this.notificationAttachmentSearchRepository = notificationAttachmentSearchRepository;
    }

    /**
     * Save a notificationAttachment.
     *
     * @param notificationAttachmentDTO the entity to save.
     * @return the persisted entity.
     */
    public NotificationAttachmentDTO save(NotificationAttachmentDTO notificationAttachmentDTO) {
        LOG.debug("Request to save NotificationAttachment : {}", notificationAttachmentDTO);
        NotificationAttachment notificationAttachment = notificationAttachmentMapper.toEntity(notificationAttachmentDTO);
        notificationAttachment = notificationAttachmentRepository.save(notificationAttachment);
        notificationAttachmentSearchRepository.index(notificationAttachment);
        return notificationAttachmentMapper.toDto(notificationAttachment);
    }

    /**
     * Update a notificationAttachment.
     *
     * @param notificationAttachmentDTO the entity to save.
     * @return the persisted entity.
     */
    public NotificationAttachmentDTO update(NotificationAttachmentDTO notificationAttachmentDTO) {
        LOG.debug("Request to update NotificationAttachment : {}", notificationAttachmentDTO);
        NotificationAttachment notificationAttachment = notificationAttachmentMapper.toEntity(notificationAttachmentDTO);
        notificationAttachment = notificationAttachmentRepository.save(notificationAttachment);
        notificationAttachmentSearchRepository.index(notificationAttachment);
        return notificationAttachmentMapper.toDto(notificationAttachment);
    }

    /**
     * Partially update a notificationAttachment.
     *
     * @param notificationAttachmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<NotificationAttachmentDTO> partialUpdate(NotificationAttachmentDTO notificationAttachmentDTO) {
        LOG.debug("Request to partially update NotificationAttachment : {}", notificationAttachmentDTO);

        return notificationAttachmentRepository
            .findById(notificationAttachmentDTO.getId())
            .map(existingNotificationAttachment -> {
                notificationAttachmentMapper.partialUpdate(existingNotificationAttachment, notificationAttachmentDTO);

                return existingNotificationAttachment;
            })
            .map(notificationAttachmentRepository::save)
            .map(savedNotificationAttachment -> {
                notificationAttachmentSearchRepository.index(savedNotificationAttachment);
                return savedNotificationAttachment;
            })
            .map(notificationAttachmentMapper::toDto);
    }

    /**
     * Get one notificationAttachment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NotificationAttachmentDTO> findOne(UUID id) {
        LOG.debug("Request to get NotificationAttachment : {}", id);
        return notificationAttachmentRepository.findById(id).map(notificationAttachmentMapper::toDto);
    }

    /**
     * Delete the notificationAttachment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(UUID id) {
        LOG.debug("Request to delete NotificationAttachment : {}", id);
        notificationAttachmentRepository.deleteById(id);
        notificationAttachmentSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the notificationAttachment corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationAttachmentDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of NotificationAttachments for query {}", query);
        return notificationAttachmentSearchRepository.search(query, pageable).map(notificationAttachmentMapper::toDto);
    }
}
