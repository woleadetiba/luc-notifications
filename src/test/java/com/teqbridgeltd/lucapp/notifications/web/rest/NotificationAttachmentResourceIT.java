package com.teqbridgeltd.lucapp.notifications.web.rest;

import static com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachmentAsserts.*;
import static com.teqbridgeltd.lucapp.notifications.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teqbridgeltd.lucapp.notifications.IntegrationTest;
import com.teqbridgeltd.lucapp.notifications.domain.Notification;
import com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachment;
import com.teqbridgeltd.lucapp.notifications.repository.NotificationAttachmentRepository;
import com.teqbridgeltd.lucapp.notifications.repository.search.NotificationAttachmentSearchRepository;
import com.teqbridgeltd.lucapp.notifications.service.dto.NotificationAttachmentDTO;
import com.teqbridgeltd.lucapp.notifications.service.mapper.NotificationAttachmentMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link NotificationAttachmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationAttachmentResourceIT {

    private static final String DEFAULT_FILE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FILE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FILE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_FILE_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_FILE_SIZE = 1L;
    private static final Long UPDATED_FILE_SIZE = 2L;
    private static final Long SMALLER_FILE_SIZE = 1L - 1L;

    private static final String DEFAULT_FILE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_FILE_PATH = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/notification-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/notification-attachments/_search";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationAttachmentRepository notificationAttachmentRepository;

    @Autowired
    private NotificationAttachmentMapper notificationAttachmentMapper;

    @Autowired
    private NotificationAttachmentSearchRepository notificationAttachmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationAttachmentMockMvc;

    private NotificationAttachment notificationAttachment;

    private NotificationAttachment insertedNotificationAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationAttachment createEntity() {
        return new NotificationAttachment()
            .fileName(DEFAULT_FILE_NAME)
            .fileType(DEFAULT_FILE_TYPE)
            .fileSize(DEFAULT_FILE_SIZE)
            .filePath(DEFAULT_FILE_PATH)
            .createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationAttachment createUpdatedEntity() {
        return new NotificationAttachment()
            .fileName(UPDATED_FILE_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .fileSize(UPDATED_FILE_SIZE)
            .filePath(UPDATED_FILE_PATH)
            .createdAt(UPDATED_CREATED_AT);
    }

    @BeforeEach
    void initTest() {
        notificationAttachment = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedNotificationAttachment != null) {
            notificationAttachmentRepository.delete(insertedNotificationAttachment);
            notificationAttachmentSearchRepository.delete(insertedNotificationAttachment);
            insertedNotificationAttachment = null;
        }
    }

    @Test
    @Transactional
    void createNotificationAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        // Create the NotificationAttachment
        NotificationAttachmentDTO notificationAttachmentDTO = notificationAttachmentMapper.toDto(notificationAttachment);
        var returnedNotificationAttachmentDTO = om.readValue(
            restNotificationAttachmentMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationAttachmentDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationAttachmentDTO.class
        );

        // Validate the NotificationAttachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotificationAttachment = notificationAttachmentMapper.toEntity(returnedNotificationAttachmentDTO);
        assertNotificationAttachmentUpdatableFieldsEquals(
            returnedNotificationAttachment,
            getPersistedNotificationAttachment(returnedNotificationAttachment)
        );

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedNotificationAttachment = returnedNotificationAttachment;
    }

    @Test
    @Transactional
    void createNotificationAttachmentWithExistingId() throws Exception {
        // Create the NotificationAttachment with an existing ID
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);
        NotificationAttachmentDTO notificationAttachmentDTO = notificationAttachmentMapper.toDto(notificationAttachment);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationAttachmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the NotificationAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllNotificationAttachments() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList
        restNotificationAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationAttachment.getId().toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].fileSize").value(hasItem(DEFAULT_FILE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].filePath").value(hasItem(DEFAULT_FILE_PATH)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    @Test
    @Transactional
    void getNotificationAttachment() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get the notificationAttachment
        restNotificationAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationAttachment.getId().toString()))
            .andExpect(jsonPath("$.fileName").value(DEFAULT_FILE_NAME))
            .andExpect(jsonPath("$.fileType").value(DEFAULT_FILE_TYPE))
            .andExpect(jsonPath("$.fileSize").value(DEFAULT_FILE_SIZE.intValue()))
            .andExpect(jsonPath("$.filePath").value(DEFAULT_FILE_PATH))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNotificationAttachmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        UUID id = notificationAttachment.getId();

        defaultNotificationAttachmentFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileName equals to
        defaultNotificationAttachmentFiltering("fileName.equals=" + DEFAULT_FILE_NAME, "fileName.equals=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileName in
        defaultNotificationAttachmentFiltering(
            "fileName.in=" + DEFAULT_FILE_NAME + "," + UPDATED_FILE_NAME,
            "fileName.in=" + UPDATED_FILE_NAME
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileName is not null
        defaultNotificationAttachmentFiltering("fileName.specified=true", "fileName.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileNameContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileName contains
        defaultNotificationAttachmentFiltering("fileName.contains=" + DEFAULT_FILE_NAME, "fileName.contains=" + UPDATED_FILE_NAME);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileName does not contain
        defaultNotificationAttachmentFiltering(
            "fileName.doesNotContain=" + UPDATED_FILE_NAME,
            "fileName.doesNotContain=" + DEFAULT_FILE_NAME
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileType equals to
        defaultNotificationAttachmentFiltering("fileType.equals=" + DEFAULT_FILE_TYPE, "fileType.equals=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileType in
        defaultNotificationAttachmentFiltering(
            "fileType.in=" + DEFAULT_FILE_TYPE + "," + UPDATED_FILE_TYPE,
            "fileType.in=" + UPDATED_FILE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileType is not null
        defaultNotificationAttachmentFiltering("fileType.specified=true", "fileType.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileTypeContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileType contains
        defaultNotificationAttachmentFiltering("fileType.contains=" + DEFAULT_FILE_TYPE, "fileType.contains=" + UPDATED_FILE_TYPE);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileTypeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileType does not contain
        defaultNotificationAttachmentFiltering(
            "fileType.doesNotContain=" + UPDATED_FILE_TYPE,
            "fileType.doesNotContain=" + DEFAULT_FILE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileSizeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileSize equals to
        defaultNotificationAttachmentFiltering("fileSize.equals=" + DEFAULT_FILE_SIZE, "fileSize.equals=" + UPDATED_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileSizeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileSize in
        defaultNotificationAttachmentFiltering(
            "fileSize.in=" + DEFAULT_FILE_SIZE + "," + UPDATED_FILE_SIZE,
            "fileSize.in=" + UPDATED_FILE_SIZE
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileSizeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileSize is not null
        defaultNotificationAttachmentFiltering("fileSize.specified=true", "fileSize.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileSizeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileSize is greater than or equal to
        defaultNotificationAttachmentFiltering(
            "fileSize.greaterThanOrEqual=" + DEFAULT_FILE_SIZE,
            "fileSize.greaterThanOrEqual=" + UPDATED_FILE_SIZE
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileSizeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileSize is less than or equal to
        defaultNotificationAttachmentFiltering(
            "fileSize.lessThanOrEqual=" + DEFAULT_FILE_SIZE,
            "fileSize.lessThanOrEqual=" + SMALLER_FILE_SIZE
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileSizeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileSize is less than
        defaultNotificationAttachmentFiltering("fileSize.lessThan=" + UPDATED_FILE_SIZE, "fileSize.lessThan=" + DEFAULT_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFileSizeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where fileSize is greater than
        defaultNotificationAttachmentFiltering("fileSize.greaterThan=" + SMALLER_FILE_SIZE, "fileSize.greaterThan=" + DEFAULT_FILE_SIZE);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFilePathIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where filePath equals to
        defaultNotificationAttachmentFiltering("filePath.equals=" + DEFAULT_FILE_PATH, "filePath.equals=" + UPDATED_FILE_PATH);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFilePathIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where filePath in
        defaultNotificationAttachmentFiltering(
            "filePath.in=" + DEFAULT_FILE_PATH + "," + UPDATED_FILE_PATH,
            "filePath.in=" + UPDATED_FILE_PATH
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFilePathIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where filePath is not null
        defaultNotificationAttachmentFiltering("filePath.specified=true", "filePath.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFilePathContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where filePath contains
        defaultNotificationAttachmentFiltering("filePath.contains=" + DEFAULT_FILE_PATH, "filePath.contains=" + UPDATED_FILE_PATH);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByFilePathNotContainsSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where filePath does not contain
        defaultNotificationAttachmentFiltering(
            "filePath.doesNotContain=" + UPDATED_FILE_PATH,
            "filePath.doesNotContain=" + DEFAULT_FILE_PATH
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where createdAt equals to
        defaultNotificationAttachmentFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where createdAt in
        defaultNotificationAttachmentFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        // Get all the notificationAttachmentList where createdAt is not null
        defaultNotificationAttachmentFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllNotificationAttachmentsByNotificationIsEqualToSomething() throws Exception {
        Notification notification;
        if (TestUtil.findAll(em, Notification.class).isEmpty()) {
            notificationAttachmentRepository.saveAndFlush(notificationAttachment);
            notification = NotificationResourceIT.createEntity();
        } else {
            notification = TestUtil.findAll(em, Notification.class).get(0);
        }
        em.persist(notification);
        em.flush();
        notificationAttachment.setNotification(notification);
        notificationAttachmentRepository.saveAndFlush(notificationAttachment);
        UUID notificationId = notification.getId();
        // Get all the notificationAttachmentList where notification equals to notificationId
        defaultNotificationAttachmentShouldBeFound("notificationId.equals=" + notificationId);

        // Get all the notificationAttachmentList where notification equals to UUID.randomUUID()
        defaultNotificationAttachmentShouldNotBeFound("notificationId.equals=" + UUID.randomUUID());
    }

    private void defaultNotificationAttachmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultNotificationAttachmentShouldBeFound(shouldBeFound);
        defaultNotificationAttachmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationAttachmentShouldBeFound(String filter) throws Exception {
        restNotificationAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationAttachment.getId().toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].fileSize").value(hasItem(DEFAULT_FILE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].filePath").value(hasItem(DEFAULT_FILE_PATH)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));

        // Check, that the count call also returns 1
        restNotificationAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationAttachmentShouldNotBeFound(String filter) throws Exception {
        restNotificationAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingNotificationAttachment() throws Exception {
        // Get the notificationAttachment
        restNotificationAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotificationAttachment() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationAttachmentSearchRepository.save(notificationAttachment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());

        // Update the notificationAttachment
        NotificationAttachment updatedNotificationAttachment = notificationAttachmentRepository
            .findById(notificationAttachment.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedNotificationAttachment are not directly saved in db
        em.detach(updatedNotificationAttachment);
        updatedNotificationAttachment
            .fileName(UPDATED_FILE_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .fileSize(UPDATED_FILE_SIZE)
            .filePath(UPDATED_FILE_PATH)
            .createdAt(UPDATED_CREATED_AT);
        NotificationAttachmentDTO notificationAttachmentDTO = notificationAttachmentMapper.toDto(updatedNotificationAttachment);

        restNotificationAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationAttachmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the NotificationAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationAttachmentToMatchAllProperties(updatedNotificationAttachment);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<NotificationAttachment> notificationAttachmentSearchList = Streamable.of(
                    notificationAttachmentSearchRepository.findAll()
                ).toList();
                NotificationAttachment testNotificationAttachmentSearch = notificationAttachmentSearchList.get(searchDatabaseSizeAfter - 1);

                assertNotificationAttachmentAllPropertiesEquals(testNotificationAttachmentSearch, updatedNotificationAttachment);
            });
    }

    @Test
    @Transactional
    void putNonExistingNotificationAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        notificationAttachment.setId(UUID.randomUUID());

        // Create the NotificationAttachment
        NotificationAttachmentDTO notificationAttachmentDTO = notificationAttachmentMapper.toDto(notificationAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificationAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        notificationAttachment.setId(UUID.randomUUID());

        // Create the NotificationAttachment
        NotificationAttachmentDTO notificationAttachmentDTO = notificationAttachmentMapper.toDto(notificationAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificationAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        notificationAttachment.setId(UUID.randomUUID());

        // Create the NotificationAttachment
        NotificationAttachmentDTO notificationAttachmentDTO = notificationAttachmentMapper.toDto(notificationAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationAttachmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateNotificationAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationAttachment using partial update
        NotificationAttachment partialUpdatedNotificationAttachment = new NotificationAttachment();
        partialUpdatedNotificationAttachment.setId(notificationAttachment.getId());

        partialUpdatedNotificationAttachment.fileSize(UPDATED_FILE_SIZE);

        restNotificationAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationAttachment))
            )
            .andExpect(status().isOk());

        // Validate the NotificationAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotificationAttachment, notificationAttachment),
            getPersistedNotificationAttachment(notificationAttachment)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotificationAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationAttachment using partial update
        NotificationAttachment partialUpdatedNotificationAttachment = new NotificationAttachment();
        partialUpdatedNotificationAttachment.setId(notificationAttachment.getId());

        partialUpdatedNotificationAttachment
            .fileName(UPDATED_FILE_NAME)
            .fileType(UPDATED_FILE_TYPE)
            .fileSize(UPDATED_FILE_SIZE)
            .filePath(UPDATED_FILE_PATH)
            .createdAt(UPDATED_CREATED_AT);

        restNotificationAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationAttachment))
            )
            .andExpect(status().isOk());

        // Validate the NotificationAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationAttachmentUpdatableFieldsEquals(
            partialUpdatedNotificationAttachment,
            getPersistedNotificationAttachment(partialUpdatedNotificationAttachment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingNotificationAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        notificationAttachment.setId(UUID.randomUUID());

        // Create the NotificationAttachment
        NotificationAttachmentDTO notificationAttachmentDTO = notificationAttachmentMapper.toDto(notificationAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationAttachmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificationAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        notificationAttachment.setId(UUID.randomUUID());

        // Create the NotificationAttachment
        NotificationAttachmentDTO notificationAttachmentDTO = notificationAttachmentMapper.toDto(notificationAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificationAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        notificationAttachment.setId(UUID.randomUUID());

        // Create the NotificationAttachment
        NotificationAttachmentDTO notificationAttachmentDTO = notificationAttachmentMapper.toDto(notificationAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(notificationAttachmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteNotificationAttachment() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);
        notificationAttachmentRepository.save(notificationAttachment);
        notificationAttachmentSearchRepository.save(notificationAttachment);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the notificationAttachment
        restNotificationAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationAttachment.getId().toString()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(notificationAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchNotificationAttachment() throws Exception {
        // Initialize the database
        insertedNotificationAttachment = notificationAttachmentRepository.saveAndFlush(notificationAttachment);
        notificationAttachmentSearchRepository.save(notificationAttachment);

        // Search the notificationAttachment
        restNotificationAttachmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + notificationAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationAttachment.getId().toString())))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.[*].fileType").value(hasItem(DEFAULT_FILE_TYPE)))
            .andExpect(jsonPath("$.[*].fileSize").value(hasItem(DEFAULT_FILE_SIZE.intValue())))
            .andExpect(jsonPath("$.[*].filePath").value(hasItem(DEFAULT_FILE_PATH)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return notificationAttachmentRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected NotificationAttachment getPersistedNotificationAttachment(NotificationAttachment notificationAttachment) {
        return notificationAttachmentRepository.findById(notificationAttachment.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationAttachmentToMatchAllProperties(NotificationAttachment expectedNotificationAttachment) {
        assertNotificationAttachmentAllPropertiesEquals(
            expectedNotificationAttachment,
            getPersistedNotificationAttachment(expectedNotificationAttachment)
        );
    }

    protected void assertPersistedNotificationAttachmentToMatchUpdatableProperties(NotificationAttachment expectedNotificationAttachment) {
        assertNotificationAttachmentAllUpdatablePropertiesEquals(
            expectedNotificationAttachment,
            getPersistedNotificationAttachment(expectedNotificationAttachment)
        );
    }
}
