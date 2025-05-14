package com.teqbridgeltd.lucapp.notifications.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DictionaryCriteriaTest {

    @Test
    void newDictionaryCriteriaHasAllFiltersNullTest() {
        var dictionaryCriteria = new DictionaryCriteria();
        assertThat(dictionaryCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void dictionaryCriteriaFluentMethodsCreatesFiltersTest() {
        var dictionaryCriteria = new DictionaryCriteria();

        setAllFilters(dictionaryCriteria);

        assertThat(dictionaryCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void dictionaryCriteriaCopyCreatesNullFilterTest() {
        var dictionaryCriteria = new DictionaryCriteria();
        var copy = dictionaryCriteria.copy();

        assertThat(dictionaryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(dictionaryCriteria)
        );
    }

    @Test
    void dictionaryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var dictionaryCriteria = new DictionaryCriteria();
        setAllFilters(dictionaryCriteria);

        var copy = dictionaryCriteria.copy();

        assertThat(dictionaryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(dictionaryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var dictionaryCriteria = new DictionaryCriteria();

        assertThat(dictionaryCriteria).hasToString("DictionaryCriteria{}");
    }

    private static void setAllFilters(DictionaryCriteria dictionaryCriteria) {
        dictionaryCriteria.id();
        dictionaryCriteria.keyName();
        dictionaryCriteria.keyCode();
        dictionaryCriteria.label();
        dictionaryCriteria.description();
        dictionaryCriteria.notificationId();
        dictionaryCriteria.distinct();
    }

    private static Condition<DictionaryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getKeyName()) &&
                condition.apply(criteria.getKeyCode()) &&
                condition.apply(criteria.getLabel()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getNotificationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DictionaryCriteria> copyFiltersAre(DictionaryCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getKeyName(), copy.getKeyName()) &&
                condition.apply(criteria.getKeyCode(), copy.getKeyCode()) &&
                condition.apply(criteria.getLabel(), copy.getLabel()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getNotificationId(), copy.getNotificationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
