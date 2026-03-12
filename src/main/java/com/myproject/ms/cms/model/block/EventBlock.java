package com.myproject.ms.cms.model.block;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@TypeAlias("event")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class EventBlock extends Block {
    private String subTitle;
    private Content content;
}
