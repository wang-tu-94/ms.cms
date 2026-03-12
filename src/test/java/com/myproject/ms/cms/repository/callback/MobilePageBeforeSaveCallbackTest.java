package com.myproject.ms.cms.repository.callback;

import com.myproject.ms.cms.model.MobilePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
@Import(MobilePageBeforeSaveCallback.class)
class MobilePageBeforeSaveCallbackTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0").withExposedPorts(27017);

    @Test
    @DisplayName("Should initialize publishedDate to now if null on creation")
    void shouldInitializePublishedDateOnCreation() {
        MobilePage page = new MobilePage();
        page.setName("Test Page");

        MobilePage savedPage = mongoTemplate.save(page);

        assertThat(savedPage.getId()).isNotNull();
        assertThat(savedPage.getPublishedDate())
                .isNotNull()
                .isBeforeOrEqualTo(Instant.now());
    }

    @Test
    @DisplayName("Should not override publishedDate if already provided")
    void shouldNotOverrideExistingPublishedDate() {
        Instant specificDate = Instant.parse("2026-01-01T10:00:00Z");
        MobilePage page = new MobilePage();
        page.setName("Manual Page");
        page.setPublishedDate(specificDate);

        MobilePage savedPage = mongoTemplate.save(page);

        assertThat(savedPage.getPublishedDate()).isEqualTo(specificDate);
    }
}