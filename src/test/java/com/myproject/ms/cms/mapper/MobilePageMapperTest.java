package com.myproject.ms.cms.mapper;

import com.myproject.ms.cms.dto.MobilePageDto;
import com.myproject.ms.cms.model.MobilePage;
import com.myproject.ms.cms.model.State;
import com.myproject.ms.cms.model.block.ContentListBlock;
import com.myproject.ms.cms.model.block.EventBlock;
import com.myproject.ms.cms.model.block.TextBlock;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MobilePageMapperTest {
    private final MobilePageMapper mapper = new MobilePageMapperImpl();

    @Test
    void shouldMapEntityToDto() {
        ObjectId id = new ObjectId();
        Instant now = Instant.now();

        MobilePage entity = new MobilePage();
        entity.setId(id);
        entity.setName("Page de test");
        entity.setState(State.ONLINE);
        entity.setPublishedDate(now);
        entity.setBlocks(List.of(new ContentListBlock(), new EventBlock(), new TextBlock()));

        MobilePageDto dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(id.toHexString());
        assertThat(dto.name()).isEqualTo("Page de test");
        assertThat(dto.state()).isEqualTo(State.ONLINE);
        assertThat(dto.publishedDate()).isEqualTo(now);
        assertThat(dto.blocks()).hasSize(3);
        assertThat(dto.blocks().getFirst()).isInstanceOf(ContentListBlock.class);
    }

    @Test
    void shouldMapDtoToEntity() {
        String stringId = "65a1b2c3d4e5f6a7b8c9d0e1";
        Instant now = Instant.now();

        MobilePageDto dto = new MobilePageDto(
                stringId,
                State.OFFLINE,
                "Nouvelle Page DTO",
                null,
                now,
                Collections.emptyList()
        );

        MobilePage entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(new ObjectId(stringId));
        assertThat(entity.getName()).isEqualTo("Nouvelle Page DTO");
        assertThat(entity.getState()).isEqualTo(State.OFFLINE);
    }

    @Test
    void shouldMapInvalidStringToNullObjectId() {
        MobilePageDto dto = new MobilePageDto(
                "invalid-id-format",
                State.OFFLINE,
                "Page",
                null, null, null
        );

        MobilePage entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
    }
}