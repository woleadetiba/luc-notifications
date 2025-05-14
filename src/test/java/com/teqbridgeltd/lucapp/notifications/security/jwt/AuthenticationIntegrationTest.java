package com.teqbridgeltd.lucapp.notifications.security.jwt;

import com.teqbridgeltd.lucapp.notifications.config.SecurityConfiguration;
import com.teqbridgeltd.lucapp.notifications.config.SecurityJwtConfiguration;
import com.teqbridgeltd.lucapp.notifications.config.WebConfigurer;
import com.teqbridgeltd.lucapp.notifications.management.SecurityMetersService;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import tech.jhipster.config.JHipsterProperties;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        JHipsterProperties.class,
        WebConfigurer.class,
        SecurityConfiguration.class,
        SecurityJwtConfiguration.class,
        SecurityMetersService.class,
        JwtAuthenticationTestUtils.class,
    }
)
public @interface AuthenticationIntegrationTest {
}
