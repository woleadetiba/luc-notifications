package com.teqbridgeltd.lucapp.notifications.web.rest;

import static com.teqbridgeltd.lucapp.notifications.domain.DictionaryAsserts.*;
import static com.teqbridgeltd.lucapp.notifications.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teqbridgeltd.lucapp.notifications.IntegrationTest;
import com.teqbridgeltd.lucapp.notifications.domain.Dictionary;
import com.teqbridgeltd.lucapp.notifications.repository.DictionaryRepository;
import com.teqbridgeltd.lucapp.notifications.repository.search.DictionarySearchRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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
 * Integration tests for the {@link DictionaryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DictionaryResourceIT {

    private static final String DEFAULT_KEY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_KEY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_KEY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_KEY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/dictionaries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/dictionaries/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictionarySearchRepository dictionarySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDictionaryMockMvc;

    private Dictionary dictionary;

    private Dictionary insertedDictionary;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dictionary createEntity() {
        return new Dictionary().keyName(DEFAULT_KEY_NAME).keyCode(DEFAULT_KEY_CODE).label(DEFAULT_LABEL).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dictionary createUpdatedEntity() {
        return new Dictionary().keyName(UPDATED_KEY_NAME).keyCode(UPDATED_KEY_CODE).label(UPDATED_LABEL).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        dictionary = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDictionary != null) {
            dictionaryRepository.delete(insertedDictionary);
            dictionarySearchRepository.delete(insertedDictionary);
            insertedDictionary = null;
        }
    }

    @Test
    @Transactional
    void createDictionary() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        // Create the Dictionary
        var returnedDictionary = om.readValue(
            restDictionaryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dictionary)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Dictionary.class
        );

        // Validate the Dictionary in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDictionaryUpdatableFieldsEquals(returnedDictionary, getPersistedDictionary(returnedDictionary));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDictionary = returnedDictionary;
    }

    @Test
    @Transactional
    void createDictionaryWithExistingId() throws Exception {
        // Create the Dictionary with an existing ID
        dictionary.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDictionaryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dictionary)))
            .andExpect(status().isBadRequest());

        // Validate the Dictionary in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDictionaries() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList
        restDictionaryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dictionary.getId().intValue())))
            .andExpect(jsonPath("$.[*].keyName").value(hasItem(DEFAULT_KEY_NAME)))
            .andExpect(jsonPath("$.[*].keyCode").value(hasItem(DEFAULT_KEY_CODE)))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getDictionary() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get the dictionary
        restDictionaryMockMvc
            .perform(get(ENTITY_API_URL_ID, dictionary.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(dictionary.getId().intValue()))
            .andExpect(jsonPath("$.keyName").value(DEFAULT_KEY_NAME))
            .andExpect(jsonPath("$.keyCode").value(DEFAULT_KEY_CODE))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getDictionariesByIdFiltering() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        Long id = dictionary.getId();

        defaultDictionaryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDictionaryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDictionaryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyName equals to
        defaultDictionaryFiltering("keyName.equals=" + DEFAULT_KEY_NAME, "keyName.equals=" + UPDATED_KEY_NAME);
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyName in
        defaultDictionaryFiltering("keyName.in=" + DEFAULT_KEY_NAME + "," + UPDATED_KEY_NAME, "keyName.in=" + UPDATED_KEY_NAME);
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyName is not null
        defaultDictionaryFiltering("keyName.specified=true", "keyName.specified=false");
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyNameContainsSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyName contains
        defaultDictionaryFiltering("keyName.contains=" + DEFAULT_KEY_NAME, "keyName.contains=" + UPDATED_KEY_NAME);
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyName does not contain
        defaultDictionaryFiltering("keyName.doesNotContain=" + UPDATED_KEY_NAME, "keyName.doesNotContain=" + DEFAULT_KEY_NAME);
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyCode equals to
        defaultDictionaryFiltering("keyCode.equals=" + DEFAULT_KEY_CODE, "keyCode.equals=" + UPDATED_KEY_CODE);
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyCode in
        defaultDictionaryFiltering("keyCode.in=" + DEFAULT_KEY_CODE + "," + UPDATED_KEY_CODE, "keyCode.in=" + UPDATED_KEY_CODE);
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyCode is not null
        defaultDictionaryFiltering("keyCode.specified=true", "keyCode.specified=false");
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyCode contains
        defaultDictionaryFiltering("keyCode.contains=" + DEFAULT_KEY_CODE, "keyCode.contains=" + UPDATED_KEY_CODE);
    }

    @Test
    @Transactional
    void getAllDictionariesByKeyCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where keyCode does not contain
        defaultDictionaryFiltering("keyCode.doesNotContain=" + UPDATED_KEY_CODE, "keyCode.doesNotContain=" + DEFAULT_KEY_CODE);
    }

    @Test
    @Transactional
    void getAllDictionariesByLabelIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where label equals to
        defaultDictionaryFiltering("label.equals=" + DEFAULT_LABEL, "label.equals=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllDictionariesByLabelIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where label in
        defaultDictionaryFiltering("label.in=" + DEFAULT_LABEL + "," + UPDATED_LABEL, "label.in=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllDictionariesByLabelIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where label is not null
        defaultDictionaryFiltering("label.specified=true", "label.specified=false");
    }

    @Test
    @Transactional
    void getAllDictionariesByLabelContainsSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where label contains
        defaultDictionaryFiltering("label.contains=" + DEFAULT_LABEL, "label.contains=" + UPDATED_LABEL);
    }

    @Test
    @Transactional
    void getAllDictionariesByLabelNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where label does not contain
        defaultDictionaryFiltering("label.doesNotContain=" + UPDATED_LABEL, "label.doesNotContain=" + DEFAULT_LABEL);
    }

    @Test
    @Transactional
    void getAllDictionariesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where description equals to
        defaultDictionaryFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDictionariesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where description in
        defaultDictionaryFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDictionariesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where description is not null
        defaultDictionaryFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllDictionariesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where description contains
        defaultDictionaryFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDictionariesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        // Get all the dictionaryList where description does not contain
        defaultDictionaryFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    private void defaultDictionaryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDictionaryShouldBeFound(shouldBeFound);
        defaultDictionaryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDictionaryShouldBeFound(String filter) throws Exception {
        restDictionaryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dictionary.getId().intValue())))
            .andExpect(jsonPath("$.[*].keyName").value(hasItem(DEFAULT_KEY_NAME)))
            .andExpect(jsonPath("$.[*].keyCode").value(hasItem(DEFAULT_KEY_CODE)))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restDictionaryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDictionaryShouldNotBeFound(String filter) throws Exception {
        restDictionaryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDictionaryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDictionary() throws Exception {
        // Get the dictionary
        restDictionaryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDictionary() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        dictionarySearchRepository.save(dictionary);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());

        // Update the dictionary
        Dictionary updatedDictionary = dictionaryRepository.findById(dictionary.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDictionary are not directly saved in db
        em.detach(updatedDictionary);
        updatedDictionary.keyName(UPDATED_KEY_NAME).keyCode(UPDATED_KEY_CODE).label(UPDATED_LABEL).description(UPDATED_DESCRIPTION);

        restDictionaryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDictionary.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDictionary))
            )
            .andExpect(status().isOk());

        // Validate the Dictionary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDictionaryToMatchAllProperties(updatedDictionary);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Dictionary> dictionarySearchList = Streamable.of(dictionarySearchRepository.findAll()).toList();
                Dictionary testDictionarySearch = dictionarySearchList.get(searchDatabaseSizeAfter - 1);

                assertDictionaryAllPropertiesEquals(testDictionarySearch, updatedDictionary);
            });
    }

    @Test
    @Transactional
    void putNonExistingDictionary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        dictionary.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDictionaryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, dictionary.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dictionary))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dictionary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDictionary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        dictionary.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDictionaryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(dictionary))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dictionary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDictionary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        dictionary.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDictionaryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dictionary)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dictionary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDictionaryWithPatch() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dictionary using partial update
        Dictionary partialUpdatedDictionary = new Dictionary();
        partialUpdatedDictionary.setId(dictionary.getId());

        partialUpdatedDictionary.label(UPDATED_LABEL);

        restDictionaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDictionary.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDictionary))
            )
            .andExpect(status().isOk());

        // Validate the Dictionary in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDictionaryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDictionary, dictionary),
            getPersistedDictionary(dictionary)
        );
    }

    @Test
    @Transactional
    void fullUpdateDictionaryWithPatch() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the dictionary using partial update
        Dictionary partialUpdatedDictionary = new Dictionary();
        partialUpdatedDictionary.setId(dictionary.getId());

        partialUpdatedDictionary.keyName(UPDATED_KEY_NAME).keyCode(UPDATED_KEY_CODE).label(UPDATED_LABEL).description(UPDATED_DESCRIPTION);

        restDictionaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDictionary.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDictionary))
            )
            .andExpect(status().isOk());

        // Validate the Dictionary in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDictionaryUpdatableFieldsEquals(partialUpdatedDictionary, getPersistedDictionary(partialUpdatedDictionary));
    }

    @Test
    @Transactional
    void patchNonExistingDictionary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        dictionary.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDictionaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, dictionary.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dictionary))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dictionary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDictionary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        dictionary.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDictionaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(dictionary))
            )
            .andExpect(status().isBadRequest());

        // Validate the Dictionary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDictionary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        dictionary.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDictionaryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(dictionary)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Dictionary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDictionary() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);
        dictionaryRepository.save(dictionary);
        dictionarySearchRepository.save(dictionary);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the dictionary
        restDictionaryMockMvc
            .perform(delete(ENTITY_API_URL_ID, dictionary.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(dictionarySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDictionary() throws Exception {
        // Initialize the database
        insertedDictionary = dictionaryRepository.saveAndFlush(dictionary);
        dictionarySearchRepository.save(dictionary);

        // Search the dictionary
        restDictionaryMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + dictionary.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dictionary.getId().intValue())))
            .andExpect(jsonPath("$.[*].keyName").value(hasItem(DEFAULT_KEY_NAME)))
            .andExpect(jsonPath("$.[*].keyCode").value(hasItem(DEFAULT_KEY_CODE)))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    protected long getRepositoryCount() {
        return dictionaryRepository.count();
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

    protected Dictionary getPersistedDictionary(Dictionary dictionary) {
        return dictionaryRepository.findById(dictionary.getId()).orElseThrow();
    }

    protected void assertPersistedDictionaryToMatchAllProperties(Dictionary expectedDictionary) {
        assertDictionaryAllPropertiesEquals(expectedDictionary, getPersistedDictionary(expectedDictionary));
    }

    protected void assertPersistedDictionaryToMatchUpdatableProperties(Dictionary expectedDictionary) {
        assertDictionaryAllUpdatablePropertiesEquals(expectedDictionary, getPersistedDictionary(expectedDictionary));
    }
}
