package com.teqbridgeltd.lucapp.notifications.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.teqbridgeltd.lucapp.notifications.domain.Notification;
import com.teqbridgeltd.lucapp.notifications.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Notification} entity.
 */
public interface NotificationSearchRepository extends ElasticsearchRepository<Notification, UUID>, NotificationSearchRepositoryInternal {}

interface NotificationSearchRepositoryInternal {
    Page<Notification> search(String query, Pageable pageable);

    Page<Notification> search(Query query);

    @Async
    void index(Notification entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class NotificationSearchRepositoryInternalImpl implements NotificationSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final NotificationRepository repository;

    NotificationSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, NotificationRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Notification> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Notification> search(Query query) {
        SearchHits<Notification> searchHits = elasticsearchTemplate.search(query, Notification.class);
        List<Notification> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Notification entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), Notification.class);
    }
}
