package com.myproject.ms.cms.model.block;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;

@TypeAlias("content-list")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ContentListBlock extends Block {
    private String subTitle;

    private List<Content> contents;
}
