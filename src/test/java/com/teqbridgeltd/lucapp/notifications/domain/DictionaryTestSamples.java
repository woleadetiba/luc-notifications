package com.teqbridgeltd.lucapp.notifications.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DictionaryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Dictionary getDictionarySample1() {
        return new Dictionary().id(1L).keyName("keyName1").keyCode("keyCode1").label("label1").description("description1");
    }

    public static Dictionary getDictionarySample2() {
        return new Dictionary().id(2L).keyName("keyName2").keyCode("keyCode2").label("label2").description("description2");
    }

    public static Dictionary getDictionaryRandomSampleGenerator() {
        return new Dictionary()
            .id(longCount.incrementAndGet())
            .keyName(UUID.randomUUID().toString())
            .keyCode(UUID.randomUUID().toString())
            .label(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
