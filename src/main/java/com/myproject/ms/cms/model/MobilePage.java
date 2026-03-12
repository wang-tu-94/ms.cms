package com.myproject.ms.cms.model;

import com.myproject.ms.cms.model.block.Block;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "mobile-pages")
@CompoundIndexes({
        @CompoundIndex(name = "state_publishedDate_idx", def = "{'state': 1, 'published_date': -1}")
})
public class MobilePage {
    @Id
    private ObjectId id;

    @Field
    @Indexed
    private State state;

    @Field
    @Indexed(name = "name_text_idx")
    private String name;

    @Field("created_date")
    @CreatedDate
    private Instant createdDate;

    @Field("updated_date")
    @LastModifiedDate
    private Instant updatedDate;

    @Field("published_date")
    @Indexed(direction = IndexDirection.DESCENDING)
    private Instant publishedDate;

    @Field
    private List<Block> blocks;
}
