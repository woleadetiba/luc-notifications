package com.teqbridgeltd.lucapp.notifications;

import com.teqbridgeltd.lucapp.notifications.config.AsyncSyncConfiguration;
import com.teqbridgeltd.lucapp.notifications.config.EmbeddedElasticsearch;
import com.teqbridgeltd.lucapp.notifications.config.EmbeddedKafka;
import com.teqbridgeltd.lucapp.notifications.config.EmbeddedRedis;
import com.teqbridgeltd.lucapp.notifications.config.EmbeddedSQL;
import com.teqbridgeltd.lucapp.notifications.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { LucNotificationsApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
