package com.teqbridgeltd.lucapp.notifications.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class NotificationAttachmentCriteriaTest {

    @Test
    void newNotificationAttachmentCriteriaHasAllFiltersNullTest() {
        var notificationAttachmentCriteria = new NotificationAttachmentCriteria();
        assertThat(notificationAttachmentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void notificationAttachmentCriteriaFluentMethodsCreatesFiltersTest() {
        var notificationAttachmentCriteria = new NotificationAttachmentCriteria();

        setAllFilters(notificationAttachmentCriteria);

        assertThat(notificationAttachmentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void notificationAttachmentCriteriaCopyCreatesNullFilterTest() {
        var notificationAttachmentCriteria = new NotificationAttachmentCriteria();
        var copy = notificationAttachmentCriteria.copy();

        assertThat(notificationAttachmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(notificationAttachmentCriteria)
        );
    }

    @Test
    void notificationAttachmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var notificationAttachmentCriteria = new NotificationAttachmentCriteria();
        setAllFilters(notificationAttachmentCriteria);

        var copy = notificationAttachmentCriteria.copy();

        assertThat(notificationAttachmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(notificationAttachmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var notificationAttachmentCriteria = new NotificationAttachmentCriteria();

        assertThat(notificationAttachmentCriteria).hasToString("NotificationAttachmentCriteria{}");
    }

    private static void setAllFilters(NotificationAttachmentCriteria notificationAttachmentCriteria) {
        notificationAttachmentCriteria.id();
        notificationAttachmentCriteria.fileName();
        notificationAttachmentCriteria.fileType();
        notificationAttachmentCriteria.fileSize();
        notificationAttachmentCriteria.filePath();
        notificationAttachmentCriteria.createdAt();
        notificationAttachmentCriteria.notificationId();
        notificationAttachmentCriteria.distinct();
    }

    private static Condition<NotificationAttachmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getFileName()) &&
                condition.apply(criteria.getFileType()) &&
                condition.apply(criteria.getFileSize()) &&
                condition.apply(criteria.getFilePath()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getNotificationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<NotificationAttachmentCriteria> copyFiltersAre(
        NotificationAttachmentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getFileName(), copy.getFileName()) &&
                condition.apply(criteria.getFileType(), copy.getFileType()) &&
                condition.apply(criteria.getFileSize(), copy.getFileSize()) &&
                condition.apply(criteria.getFilePath(), copy.getFilePath()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getNotificationId(), copy.getNotificationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
