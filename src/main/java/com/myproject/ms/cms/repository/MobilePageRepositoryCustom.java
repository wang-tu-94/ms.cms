package com.myproject.ms.cms.repository;

import com.myproject.ms.cms.model.MobilePage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MobilePageRepositoryCustom {
    Page<MobilePage> searchByFilter(MobilePageFilter filter, Pageable pageable);
}
