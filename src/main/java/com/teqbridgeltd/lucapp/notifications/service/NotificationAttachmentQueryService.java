package com.teqbridgeltd.lucapp.notifications.service;

import com.teqbridgeltd.lucapp.notifications.domain.*; // for static metamodels
import com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachment;
import com.teqbridgeltd.lucapp.notifications.repository.NotificationAttachmentRepository;
import com.teqbridgeltd.lucapp.notifications.repository.search.NotificationAttachmentSearchRepository;
import com.teqbridgeltd.lucapp.notifications.service.criteria.NotificationAttachmentCriteria;
import com.teqbridgeltd.lucapp.notifications.service.dto.NotificationAttachmentDTO;
import com.teqbridgeltd.lucapp.notifications.service.mapper.NotificationAttachmentMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link NotificationAttachment} entities in the database.
 * The main input is a {@link NotificationAttachmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link NotificationAttachmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class NotificationAttachmentQueryService extends QueryService<NotificationAttachment> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationAttachmentQueryService.class);

    private final NotificationAttachmentRepository notificationAttachmentRepository;

    private final NotificationAttachmentMapper notificationAttachmentMapper;

    private final NotificationAttachmentSearchRepository notificationAttachmentSearchRepository;

    public NotificationAttachmentQueryService(
        NotificationAttachmentRepository notificationAttachmentRepository,
        NotificationAttachmentMapper notificationAttachmentMapper,
        NotificationAttachmentSearchRepository notificationAttachmentSearchRepository
    ) {
        this.notificationAttachmentRepository = notificationAttachmentRepository;
        this.notificationAttachmentMapper = notificationAttachmentMapper;
        this.notificationAttachmentSearchRepository = notificationAttachmentSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link NotificationAttachmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationAttachmentDTO> findByCriteria(NotificationAttachmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<NotificationAttachment> specification = createSpecification(criteria);
        return notificationAttachmentRepository.findAll(specification, page).map(notificationAttachmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(NotificationAttachmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<NotificationAttachment> specification = createSpecification(criteria);
        return notificationAttachmentRepository.count(specification);
    }

    /**
     * Function to convert {@link NotificationAttachmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<NotificationAttachment> createSpecification(NotificationAttachmentCriteria criteria) {
        Specification<NotificationAttachment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildSpecification(criteria.getId(), NotificationAttachment_.id),
                buildStringSpecification(criteria.getFileName(), NotificationAttachment_.fileName),
                buildStringSpecification(criteria.getFileType(), NotificationAttachment_.fileType),
                buildRangeSpecification(criteria.getFileSize(), NotificationAttachment_.fileSize),
                buildStringSpecification(criteria.getFilePath(), NotificationAttachment_.filePath),
                buildRangeSpecification(criteria.getCreatedAt(), NotificationAttachment_.createdAt),
                buildSpecification(criteria.getNotificationId(), root ->
                    root.join(NotificationAttachment_.notification, JoinType.LEFT).get(Notification_.id)
                )
            );
        }
        return specification;
    }
}
