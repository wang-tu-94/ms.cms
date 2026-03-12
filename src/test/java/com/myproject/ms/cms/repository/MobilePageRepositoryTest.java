package com.myproject.ms.cms.repository;

import com.myproject.ms.cms.model.MobilePage;
import com.myproject.ms.cms.model.block.Block;
import com.myproject.ms.cms.model.block.ImageBlock;
import com.myproject.ms.cms.model.block.TextBlock;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
class MobilePageRepositoryTest {
    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(MobilePage.class);
    }

    @Test
    @DisplayName("Should save and load polymorphic blocks correctly in MongoDB")
    void shouldSaveAndLoadPolymorphicBlocks() {
        MobilePage page = new MobilePage();
        page.setName("Page Polymorphe");
        page.setBlocks(List.of(
                TextBlock.builder().text("Mon super texte").build(),
                ImageBlock.builder().imageUrl("https://example.com/image.png").build()
        ));

        mongoTemplate.save(page);
        MobilePage loadedPage = mongoTemplate.findById(page.getId(), MobilePage.class);

        assertThat(loadedPage).isNotNull();
        assertThat(loadedPage.getBlocks()).hasSize(2);

        Block firstBlock = loadedPage.getBlocks().get(0);
        assertThat(firstBlock).isInstanceOf(TextBlock.class);
        assertThat(((TextBlock) firstBlock).getText()).isEqualTo("Mon super texte");

        Block secondBlock = loadedPage.getBlocks().get(1);
        assertThat(secondBlock).isInstanceOf(ImageBlock.class);
        assertThat(((ImageBlock) secondBlock).getImageUrl()).isEqualTo("https://example.com/image.png");
    }

    @Test
    @DisplayName("Should store clean TypeAlias in BSON document")
    void shouldStoreCleanTypeAlias() {
        MobilePage page = new MobilePage();
        page.setBlocks(List.of(TextBlock.builder().text("text").build()));
        mongoTemplate.save(page);

        Document rawDocument = mongoTemplate.getCollection(mongoTemplate.getCollectionName(MobilePage.class))
                .find()
                .first();

        assertThat(rawDocument).isNotNull();

        @SuppressWarnings("unchecked")
        List<Document> rawBlocks = (List<Document>) rawDocument.get("blocks");

        assertThat(rawBlocks).hasSize(1);
        assertThat(rawBlocks.get(0).getString("_class")).isEqualTo("text");
    }
}