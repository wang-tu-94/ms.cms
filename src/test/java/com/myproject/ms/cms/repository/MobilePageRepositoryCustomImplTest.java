package com.myproject.ms.cms.repository;

import com.myproject.ms.cms.model.MobilePage;
import com.myproject.ms.cms.model.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
class MobilePageRepositoryCustomImplTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MobilePageRepository mobilePageRepositoryCustom;

    private MobilePage page1;
    private MobilePage page2;
    private MobilePage page3;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(MobilePage.class);

        page1 = new MobilePage();
        page1.setName("Promo Printemps");
        page1.setState(State.ONLINE); // Remplace par ton enum
        page1.setPublishedDate(Instant.parse("2026-03-01T10:00:00Z"));

        page2 = new MobilePage();
        page2.setName("Solde Hiver");
        page2.setState(State.OFFLINE);
        page2.setPublishedDate(Instant.parse("2025-12-01T10:00:00Z"));

        page3 = new MobilePage();
        page3.setName("PROMO été");
        page3.setState(State.ONLINE);
        page3.setPublishedDate(Instant.parse("2026-06-01T10:00:00Z"));

        mongoTemplate.insertAll(List.of(page1, page2, page3));
    }

    @Test
    @DisplayName("Should find pages by name with case-insensitive regex")
    void shouldFindByNameCaseInsensitive() {
        MobilePageFilter filter = new MobilePageFilter("promo", null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        Page<MobilePage> result = mobilePageRepositoryCustom.searchByFilter(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(MobilePage::getName)
                .containsExactlyInAnyOrder("Promo Printemps", "PROMO été");
    }

    @Test
    @DisplayName("Should find pages by exact state")
    void shouldFindByState() {
        MobilePageFilter filter = new MobilePageFilter(null, State.OFFLINE, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        Page<MobilePage> result = mobilePageRepositoryCustom.searchByFilter(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Solde Hiver");
    }

    @Test
    @DisplayName("Should find pages published after a specific date")
    void shouldFindByAfterPublishedDate() {
        Instant targetDate = Instant.parse("2026-01-01T00:00:00Z");
        MobilePageFilter filter = new MobilePageFilter(null, null, targetDate, null);
        Pageable pageable = PageRequest.of(0, 10);

        Page<MobilePage> result = mobilePageRepositoryCustom.searchByFilter(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(MobilePage::getName)
                .containsExactlyInAnyOrder("Promo Printemps", "PROMO été");
    }

    @Test
    @DisplayName("Should find pages by a list of ObjectIds")
    void shouldFindByIds() {
        List<String> ids = List.of(page1.getId().toHexString(), page3.getId().toHexString(), "invalid-id-format");
        MobilePageFilter filter = new MobilePageFilter(null, null, null, ids);
        Pageable pageable = PageRequest.of(0, 10);

        Page<MobilePage> result = mobilePageRepositoryCustom.searchByFilter(filter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);

        assertThat(result.getContent())
                .extracting(MobilePage::getId)
                .containsExactlyInAnyOrder(page1.getId(), page3.getId());
    }

    @Test
    @DisplayName("Should apply pagination correctly")
    void shouldPaginateResults() {
        MobilePageFilter emptyFilter = new MobilePageFilter(null, null, null, null);
        Pageable pageable = PageRequest.of(0, 2);

        Page<MobilePage> result = mobilePageRepositoryCustom.searchByFilter(emptyFilter, pageable);

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
    }
}