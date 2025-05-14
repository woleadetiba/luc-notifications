package com.teqbridgeltd.lucapp.notifications.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.teqbridgeltd.lucapp.notifications.domain.Dictionary;
import com.teqbridgeltd.lucapp.notifications.repository.DictionaryRepository;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Dictionary} entity.
 */
public interface DictionarySearchRepository extends ElasticsearchRepository<Dictionary, Long>, DictionarySearchRepositoryInternal {}

interface DictionarySearchRepositoryInternal {
    Stream<Dictionary> search(String query);

    Stream<Dictionary> search(Query query);

    @Async
    void index(Dictionary entity);

    @Async
    void deleteFromIndexById(Long id);
}

class DictionarySearchRepositoryInternalImpl implements DictionarySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final DictionaryRepository repository;

    DictionarySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, DictionaryRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Stream<Dictionary> search(String query) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery);
    }

    @Override
    public Stream<Dictionary> search(Query query) {
        return elasticsearchTemplate.search(query, Dictionary.class).map(SearchHit::getContent).stream();
    }

    @Override
    public void index(Dictionary entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Dictionary.class);
    }
}
