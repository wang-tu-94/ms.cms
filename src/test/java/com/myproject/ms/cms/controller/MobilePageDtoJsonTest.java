package com.myproject.ms.cms.controller;

import com.myproject.ms.cms.dto.MobilePageDto;
import com.myproject.ms.cms.model.State;
import com.myproject.ms.cms.model.block.ImageBlock;
import com.myproject.ms.cms.model.block.TextBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MobilePageDtoJsonTest {
    @Autowired
    private JacksonTester<MobilePageDto> json;

    @Test
    @DisplayName("Should deserialize JSON with polymorphic blocks into correct Java subclasses")
    void shouldDeserializeJsonToDto() throws Exception {
        // GIVEN : Un JSON brut avec le discriminant "type"
        String jsonContent = """
                {
                  "name": "Spring 7 Page",
                  "state": "OFFLINE",
                  "blocks": [
                    {
                      "type": "text",
                      "text": "Hello Spring Boot 4"
                    },
                    {
                      "type": "image",
                      "imageUrl": "https://img.jpg"
                    }
                  ]
                }
                """;

        // WHEN
        MobilePageDto dto = json.parseObject(jsonContent);

        // THEN
        assertThat(dto.name()).isEqualTo("Spring 7 Page");
        assertThat(dto.blocks()).hasSize(2);

        // Vérification du TextBlock
        assertThat(dto.blocks().get(0)).isInstanceOf(TextBlock.class);
        TextBlock text = (TextBlock) dto.blocks().get(0);
        assertThat(text.getText()).isEqualTo("Hello Spring Boot 4");

        // Vérification de l'ImageBlock
        assertThat(dto.blocks().get(1)).isInstanceOf(ImageBlock.class);
        ImageBlock img = (ImageBlock) dto.blocks().get(1);
        assertThat(img.getImageUrl()).isEqualTo("https://img.jpg");
    }

    @Test
    @DisplayName("Should serialize DTO to JSON containing the 'type' field")
    void shouldSerializeDtoToJson() throws Exception {
        // GIVEN
        Instant now = Instant.parse("2026-03-11T15:00:00Z");
        MobilePageDto dto = new MobilePageDto(
                "id-123",
                State.ONLINE,
                "Polymorphic Page",
                now,
                null,
                List.of(TextBlock.builder().text("Some text").build())
        );

        // WHEN & THEN
        // On vérifie que le champ "type" est bien généré dans le JSON de sortie
        assertThat(json.write(dto))
                .hasJsonPathStringValue("$.blocks[0].type", "TEXT")
                .hasJsonPathStringValue("$.blocks[0].text", "Some text");
    }
}
