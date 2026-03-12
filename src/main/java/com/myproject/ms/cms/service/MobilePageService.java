package com.myproject.ms.cms.service;

import com.myproject.ms.cms.dto.MobilePageDto;
import com.myproject.ms.cms.repository.MobilePageFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MobilePageService {
    Page<MobilePageDto> searchByQuery(MobilePageFilter mobilePageFilter, Pageable pageable);

    MobilePageDto findById(String id);

    MobilePageDto save(MobilePageDto mobilePageDto);
}
