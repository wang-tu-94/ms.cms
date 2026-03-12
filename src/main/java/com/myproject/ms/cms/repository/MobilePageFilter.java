package com.myproject.ms.cms.repository;

import com.myproject.ms.cms.model.State;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record MobilePageFilter(
        String name,
        State state,
        Instant afterPublishedDate,
        List<String> ids
) {}
