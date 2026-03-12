package com.myproject.ms.cms.model.block;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // Le champ qui sera présent dans le JSON
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContentListBlock.class, name = "content-list"),
        @JsonSubTypes.Type(value = EventBlock.class, name = "event"),
        @JsonSubTypes.Type(value = ImageBlock.class, name = "image"),
        @JsonSubTypes.Type(value = TextBlock.class, name = "text")
})
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class Block {
    private String title;
    private String description;
}
