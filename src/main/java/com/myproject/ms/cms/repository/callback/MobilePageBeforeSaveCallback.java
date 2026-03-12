package com.myproject.ms.cms.repository.callback;

import com.myproject.ms.cms.model.MobilePage;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MobilePageBeforeSaveCallback implements BeforeConvertCallback<MobilePage> {
    @Override
    public MobilePage onBeforeConvert(MobilePage entity, String collection) {
        if (entity.getId() == null && entity.getPublishedDate() == null) {
            entity.setPublishedDate(Instant.now());
        }
        return entity;
    }
}
