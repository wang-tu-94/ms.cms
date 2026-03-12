package com.myproject.ms.cms.model.block;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@TypeAlias("image")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ImageBlock extends Block {
    private String imageId;

    private String imageUrl;
}
