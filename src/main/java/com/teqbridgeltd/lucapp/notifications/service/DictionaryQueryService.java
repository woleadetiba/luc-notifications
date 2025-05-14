package com.teqbridgeltd.lucapp.notifications.service;

import com.teqbridgeltd.lucapp.notifications.domain.*; // for static metamodels
import com.teqbridgeltd.lucapp.notifications.domain.Dictionary;
import com.teqbridgeltd.lucapp.notifications.repository.DictionaryRepository;
import com.teqbridgeltd.lucapp.notifications.repository.search.DictionarySearchRepository;
import com.teqbridgeltd.lucapp.notifications.service.criteria.DictionaryCriteria;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Dictionary} entities in the database.
 * The main input is a {@link DictionaryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Dictionary} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DictionaryQueryService extends QueryService<Dictionary> {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryQueryService.class);

    private final DictionaryRepository dictionaryRepository;

    private final DictionarySearchRepository dictionarySearchRepository;

    public DictionaryQueryService(DictionaryRepository dictionaryRepository, DictionarySearchRepository dictionarySearchRepository) {
        this.dictionaryRepository = dictionaryRepository;
        this.dictionarySearchRepository = dictionarySearchRepository;
    }

    /**
     * Return a {@link List} of {@link Dictionary} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Dictionary> findByCriteria(DictionaryCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Dictionary> specification = createSpecification(criteria);
        return dictionaryRepository.findAll(specification);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DictionaryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Dictionary> specification = createSpecification(criteria);
        return dictionaryRepository.count(specification);
    }

    /**
     * Function to convert {@link DictionaryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Dictionary> createSpecification(DictionaryCriteria criteria) {
        Specification<Dictionary> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Dictionary_.id),
                buildStringSpecification(criteria.getKeyName(), Dictionary_.keyName),
                buildStringSpecification(criteria.getKeyCode(), Dictionary_.keyCode),
                buildStringSpecification(criteria.getLabel(), Dictionary_.label),
                buildStringSpecification(criteria.getDescription(), Dictionary_.description),
                buildSpecification(criteria.getNotificationId(), root ->
                    root.join(Dictionary_.notification, JoinType.LEFT).get(Notification_.id)
                )
            );
        }
        return specification;
    }
}
