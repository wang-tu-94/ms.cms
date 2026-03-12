package com.myproject.ms.cms.dto;

import com.myproject.ms.cms.model.State;
import com.myproject.ms.cms.model.block.Block;

import java.time.Instant;
import java.util.List;

public record MobilePageDto(
        String id,
        State state,
        String name,
        Instant updatedDate,
        Instant publishedDate,
        List<Block> blocks
) {}
