package com.teqbridgeltd.lucapp.notifications.web.rest;

import com.teqbridgeltd.lucapp.notifications.domain.Dictionary;
import com.teqbridgeltd.lucapp.notifications.repository.DictionaryRepository;
import com.teqbridgeltd.lucapp.notifications.service.DictionaryQueryService;
import com.teqbridgeltd.lucapp.notifications.service.DictionaryService;
import com.teqbridgeltd.lucapp.notifications.service.criteria.DictionaryCriteria;
import com.teqbridgeltd.lucapp.notifications.web.rest.errors.BadRequestAlertException;
import com.teqbridgeltd.lucapp.notifications.web.rest.errors.ElasticsearchExceptionMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.teqbridgeltd.lucapp.notifications.domain.Dictionary}.
 */
@RestController
@RequestMapping("/api/dictionaries")
public class DictionaryResource {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryResource.class);

    private static final String ENTITY_NAME = "lucNotificationsDictionary";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DictionaryService dictionaryService;

    private final DictionaryRepository dictionaryRepository;

    private final DictionaryQueryService dictionaryQueryService;

    public DictionaryResource(
        DictionaryService dictionaryService,
        DictionaryRepository dictionaryRepository,
        DictionaryQueryService dictionaryQueryService
    ) {
        this.dictionaryService = dictionaryService;
        this.dictionaryRepository = dictionaryRepository;
        this.dictionaryQueryService = dictionaryQueryService;
    }

    /**
     * {@code POST  /dictionaries} : Create a new dictionary.
     *
     * @param dictionary the dictionary to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dictionary, or with status {@code 400 (Bad Request)} if the dictionary has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Dictionary> createDictionary(@RequestBody Dictionary dictionary) throws URISyntaxException {
        LOG.debug("REST request to save Dictionary : {}", dictionary);
        if (dictionary.getId() != null) {
            throw new BadRequestAlertException("A new dictionary cannot already have an ID", ENTITY_NAME, "idexists");
        }
        dictionary = dictionaryService.save(dictionary);
        return ResponseEntity.created(new URI("/api/dictionaries/" + dictionary.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, dictionary.getId().toString()))
            .body(dictionary);
    }

    /**
     * {@code PUT  /dictionaries/:id} : Updates an existing dictionary.
     *
     * @param id the id of the dictionary to save.
     * @param dictionary the dictionary to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dictionary,
     * or with status {@code 400 (Bad Request)} if the dictionary is not valid,
     * or with status {@code 500 (Internal Server Error)} if the dictionary couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Dictionary> updateDictionary(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Dictionary dictionary
    ) throws URISyntaxException {
        LOG.debug("REST request to update Dictionary : {}, {}", id, dictionary);
        if (dictionary.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dictionary.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dictionaryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        dictionary = dictionaryService.update(dictionary);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dictionary.getId().toString()))
            .body(dictionary);
    }

    /**
     * {@code PATCH  /dictionaries/:id} : Partial updates given fields of an existing dictionary, field will ignore if it is null
     *
     * @param id the id of the dictionary to save.
     * @param dictionary the dictionary to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated dictionary,
     * or with status {@code 400 (Bad Request)} if the dictionary is not valid,
     * or with status {@code 404 (Not Found)} if the dictionary is not found,
     * or with status {@code 500 (Internal Server Error)} if the dictionary couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Dictionary> partialUpdateDictionary(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Dictionary dictionary
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Dictionary partially : {}, {}", id, dictionary);
        if (dictionary.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dictionary.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!dictionaryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Dictionary> result = dictionaryService.partialUpdate(dictionary);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dictionary.getId().toString())
        );
    }

    /**
     * {@code GET  /dictionaries} : get all the dictionaries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dictionaries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Dictionary>> getAllDictionaries(DictionaryCriteria criteria) {
        LOG.debug("REST request to get Dictionaries by criteria: {}", criteria);

        List<Dictionary> entityList = dictionaryQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /dictionaries/count} : count all the dictionaries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDictionaries(DictionaryCriteria criteria) {
        LOG.debug("REST request to count Dictionaries by criteria: {}", criteria);
        return ResponseEntity.ok().body(dictionaryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /dictionaries/:id} : get the "id" dictionary.
     *
     * @param id the id of the dictionary to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dictionary, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Dictionary> getDictionary(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Dictionary : {}", id);
        Optional<Dictionary> dictionary = dictionaryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dictionary);
    }

    /**
     * {@code DELETE  /dictionaries/:id} : delete the "id" dictionary.
     *
     * @param id the id of the dictionary to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDictionary(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Dictionary : {}", id);
        dictionaryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /dictionaries/_search?query=:query} : search for the dictionary corresponding
     * to the query.
     *
     * @param query the query of the dictionary search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<Dictionary> searchDictionaries(@RequestParam("query") String query) {
        LOG.debug("REST request to search Dictionaries for query {}", query);
        try {
            return dictionaryService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
