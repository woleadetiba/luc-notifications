package com.teqbridgeltd.lucapp.notifications.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.teqbridgeltd.lucapp.notifications.domain.NotificationAttachment;
import com.teqbridgeltd.lucapp.notifications.repository.NotificationAttachmentRepository;
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
 * Spring Data Elasticsearch repository for the {@link NotificationAttachment} entity.
 */
public interface NotificationAttachmentSearchRepository
    extends ElasticsearchRepository<NotificationAttachment, UUID>, NotificationAttachmentSearchRepositoryInternal {}

interface NotificationAttachmentSearchRepositoryInternal {
    Page<NotificationAttachment> search(String query, Pageable pageable);

    Page<NotificationAttachment> search(Query query);

    @Async
    void index(NotificationAttachment entity);

    @Async
    void deleteFromIndexById(UUID id);
}

class NotificationAttachmentSearchRepositoryInternalImpl implements NotificationAttachmentSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final NotificationAttachmentRepository repository;

    NotificationAttachmentSearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        NotificationAttachmentRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<NotificationAttachment> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<NotificationAttachment> search(Query query) {
        SearchHits<NotificationAttachment> searchHits = elasticsearchTemplate.search(query, NotificationAttachment.class);
        List<NotificationAttachment> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(NotificationAttachment entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(UUID id) {
        elasticsearchTemplate.delete(String.valueOf(id), NotificationAttachment.class);
    }
}
