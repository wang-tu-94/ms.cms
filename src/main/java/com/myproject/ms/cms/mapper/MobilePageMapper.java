package com.myproject.ms.cms.mapper;

import com.myproject.ms.cms.dto.MobilePageDto;
import com.myproject.ms.cms.model.MobilePage;
import org.bson.types.ObjectId;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface MobilePageMapper {
    MobilePageDto toDto(MobilePage mobilePage);

    MobilePage toEntity(MobilePageDto mobilePageDto);

    default String map(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }

    default ObjectId map(String id) {
        return id != null && ObjectId.isValid(id) ? new ObjectId(id) : null;
    }
}
