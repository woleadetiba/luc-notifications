package com.teqbridgeltd.lucapp.notifications.service;

import com.teqbridgeltd.lucapp.notifications.domain.Dictionary;
import com.teqbridgeltd.lucapp.notifications.repository.DictionaryRepository;
import com.teqbridgeltd.lucapp.notifications.repository.search.DictionarySearchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.teqbridgeltd.lucapp.notifications.domain.Dictionary}.
 */
@Service
@Transactional
public class DictionaryService {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryService.class);

    private final DictionaryRepository dictionaryRepository;

    private final DictionarySearchRepository dictionarySearchRepository;

    public DictionaryService(DictionaryRepository dictionaryRepository, DictionarySearchRepository dictionarySearchRepository) {
        this.dictionaryRepository = dictionaryRepository;
        this.dictionarySearchRepository = dictionarySearchRepository;
    }

    /**
     * Save a dictionary.
     *
     * @param dictionary the entity to save.
     * @return the persisted entity.
     */
    public Dictionary save(Dictionary dictionary) {
        LOG.debug("Request to save Dictionary : {}", dictionary);
        dictionary = dictionaryRepository.save(dictionary);
        dictionarySearchRepository.index(dictionary);
        return dictionary;
    }

    /**
     * Update a dictionary.
     *
     * @param dictionary the entity to save.
     * @return the persisted entity.
     */
    public Dictionary update(Dictionary dictionary) {
        LOG.debug("Request to update Dictionary : {}", dictionary);
        dictionary = dictionaryRepository.save(dictionary);
        dictionarySearchRepository.index(dictionary);
        return dictionary;
    }

    /**
     * Partially update a dictionary.
     *
     * @param dictionary the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Dictionary> partialUpdate(Dictionary dictionary) {
        LOG.debug("Request to partially update Dictionary : {}", dictionary);

        return dictionaryRepository
            .findById(dictionary.getId())
            .map(existingDictionary -> {
                if (dictionary.getKeyName() != null) {
                    existingDictionary.setKeyName(dictionary.getKeyName());
                }
                if (dictionary.getKeyCode() != null) {
                    existingDictionary.setKeyCode(dictionary.getKeyCode());
                }
                if (dictionary.getLabel() != null) {
                    existingDictionary.setLabel(dictionary.getLabel());
                }
                if (dictionary.getDescription() != null) {
                    existingDictionary.setDescription(dictionary.getDescription());
                }

                return existingDictionary;
            })
            .map(dictionaryRepository::save)
            .map(savedDictionary -> {
                dictionarySearchRepository.index(savedDictionary);
                return savedDictionary;
            });
    }

    /**
     *  Get all the dictionaries where Notification is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Dictionary> findAllWhereNotificationIsNull() {
        LOG.debug("Request to get all dictionaries where Notification is null");
        return StreamSupport.stream(dictionaryRepository.findAll().spliterator(), false)
            .filter(dictionary -> dictionary.getNotification() == null)
            .toList();
    }

    /**
     * Get one dictionary by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Dictionary> findOne(Long id) {
        LOG.debug("Request to get Dictionary : {}", id);
        return dictionaryRepository.findById(id);
    }

    /**
     * Delete the dictionary by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Dictionary : {}", id);
        dictionaryRepository.deleteById(id);
        dictionarySearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the dictionary corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<Dictionary> search(String query) {
        LOG.debug("Request to search Dictionaries for query {}", query);
        try {
            return StreamSupport.stream(dictionarySearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
