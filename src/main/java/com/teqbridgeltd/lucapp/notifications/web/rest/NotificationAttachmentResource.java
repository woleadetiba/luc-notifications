package com.teqbridgeltd.lucapp.notifications.web.rest;

import com.teqbridgeltd.lucapp.notifications.repository.NotificationAttachmentRepository;
import com.teqbridgeltd.lucapp.notifications.service.NotificationAttachmentQueryService;
import com.teqbridgeltd.lucapp.notifications.service.NotificationAttachmentService;
import com.teqbridgeltd.lucapp.notifications.service.criteria.NotificationAttachmentCriteria;
import com.teqbridgeltd.lucapp.notifications.service.dto.NotificationAttachmentDTO;
import com.teqbridgeltd.lucapp.notifications.web.rest.errors.BadRequestAlertException;
import com.teqbridgeltd.lucapp.notifications.web.rest.errors.ElasticsearchExceptionMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachment}.
 */
@RestController
@RequestMapping("/api/notification-attachments")
public class NotificationAttachmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationAttachmentResource.class);

    private static final String ENTITY_NAME = "lucNotificationsNotificationAttachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationAttachmentService notificationAttachmentService;

    private final NotificationAttachmentRepository notificationAttachmentRepository;

    private final NotificationAttachmentQueryService notificationAttachmentQueryService;

    public NotificationAttachmentResource(
        NotificationAttachmentService notificationAttachmentService,
        NotificationAttachmentRepository notificationAttachmentRepository,
        NotificationAttachmentQueryService notificationAttachmentQueryService
    ) {
        this.notificationAttachmentService = notificationAttachmentService;
        this.notificationAttachmentRepository = notificationAttachmentRepository;
        this.notificationAttachmentQueryService = notificationAttachmentQueryService;
    }

    /**
     * {@code POST  /notification-attachments} : Create a new notificationAttachment.
     *
     * @param notificationAttachmentDTO the notificationAttachmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationAttachmentDTO, or with status {@code 400 (Bad Request)} if the notificationAttachment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<NotificationAttachmentDTO> createNotificationAttachment(
        @RequestBody NotificationAttachmentDTO notificationAttachmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save NotificationAttachment : {}", notificationAttachmentDTO);
        if (notificationAttachmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new notificationAttachment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificationAttachmentDTO = notificationAttachmentService.save(notificationAttachmentDTO);
        return ResponseEntity.created(new URI("/api/notification-attachments/" + notificationAttachmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notificationAttachmentDTO.getId().toString()))
            .body(notificationAttachmentDTO);
    }

    /**
     * {@code PUT  /notification-attachments/:id} : Updates an existing notificationAttachment.
     *
     * @param id the id of the notificationAttachmentDTO to save.
     * @param notificationAttachmentDTO the notificationAttachmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationAttachmentDTO,
     * or with status {@code 400 (Bad Request)} if the notificationAttachmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationAttachmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificationAttachmentDTO> updateNotificationAttachment(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody NotificationAttachmentDTO notificationAttachmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update NotificationAttachment : {}, {}", id, notificationAttachmentDTO);
        if (notificationAttachmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationAttachmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        notificationAttachmentDTO = notificationAttachmentService.update(notificationAttachmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationAttachmentDTO.getId().toString()))
            .body(notificationAttachmentDTO);
    }

    /**
     * {@code PATCH  /notification-attachments/:id} : Partial updates given fields of an existing notificationAttachment, field will ignore if it is null
     *
     * @param id the id of the notificationAttachmentDTO to save.
     * @param notificationAttachmentDTO the notificationAttachmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationAttachmentDTO,
     * or with status {@code 400 (Bad Request)} if the notificationAttachmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationAttachmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationAttachmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificationAttachmentDTO> partialUpdateNotificationAttachment(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody NotificationAttachmentDTO notificationAttachmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update NotificationAttachment partially : {}, {}", id, notificationAttachmentDTO);
        if (notificationAttachmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationAttachmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<NotificationAttachmentDTO> result = notificationAttachmentService.partialUpdate(notificationAttachmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationAttachmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notification-attachments} : get all the notificationAttachments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notificationAttachments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<NotificationAttachmentDTO>> getAllNotificationAttachments(
        NotificationAttachmentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get NotificationAttachments by criteria: {}", criteria);

        Page<NotificationAttachmentDTO> page = notificationAttachmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notification-attachments/count} : count all the notificationAttachments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countNotificationAttachments(NotificationAttachmentCriteria criteria) {
        LOG.debug("REST request to count NotificationAttachments by criteria: {}", criteria);
        return ResponseEntity.ok().body(notificationAttachmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /notification-attachments/:id} : get the "id" notificationAttachment.
     *
     * @param id the id of the notificationAttachmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationAttachmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationAttachmentDTO> getNotificationAttachment(@PathVariable("id") UUID id) {
        LOG.debug("REST request to get NotificationAttachment : {}", id);
        Optional<NotificationAttachmentDTO> notificationAttachmentDTO = notificationAttachmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationAttachmentDTO);
    }

    /**
     * {@code DELETE  /notification-attachments/:id} : delete the "id" notificationAttachment.
     *
     * @param id the id of the notificationAttachmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificationAttachment(@PathVariable("id") UUID id) {
        LOG.debug("REST request to delete NotificationAttachment : {}", id);
        notificationAttachmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /notification-attachments/_search?query=:query} : search for the notificationAttachment corresponding
     * to the query.
     *
     * @param query the query of the notificationAttachment search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<NotificationAttachmentDTO>> searchNotificationAttachments(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of NotificationAttachments for query {}", query);
        try {
            Page<NotificationAttachmentDTO> page = notificationAttachmentService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
