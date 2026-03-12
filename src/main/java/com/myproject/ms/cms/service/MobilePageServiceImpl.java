package com.myproject.ms.cms.service;

import com.myproject.ms.cms.dto.MobilePageDto;
import com.myproject.ms.cms.exception.BadRequestException;
import com.myproject.ms.cms.exception.NotFoundException;
import com.myproject.ms.cms.mapper.MobilePageMapper;
import com.myproject.ms.cms.model.MobilePage;
import com.myproject.ms.cms.repository.MobilePageFilter;
import com.myproject.ms.cms.repository.MobilePageRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MobilePageServiceImpl implements MobilePageService {
    private final MobilePageRepository mobilePageRepository;

    private final MobilePageMapper mobilePageMapper;

    public MobilePageServiceImpl(MobilePageRepository mobilePageRepository, MobilePageMapper mobilePageMapper) {
        this.mobilePageRepository = mobilePageRepository;
        this.mobilePageMapper = mobilePageMapper;
    }

    @Override
    public Page<MobilePageDto> searchByQuery(MobilePageFilter mobilePageFilter, Pageable pageable) {
        return mobilePageRepository.searchByFilter(mobilePageFilter, pageable)
                .map(mobilePageMapper::toDto);
    }

    @Override
    public MobilePageDto findById(String id) {
        if (id == null || !ObjectId.isValid(id)) {
            throw new BadRequestException("Le format de l'ID est invalide : " + id);
        }

        return mobilePageRepository.findById(new ObjectId(id))
                .map(mobilePageMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Page introuvable avec l'ID : " + id));
    }

    @Override
    public MobilePageDto save(MobilePageDto dto) {
        MobilePage entity = mobilePageMapper.toEntity(dto);

        MobilePage saved = mobilePageRepository.save(entity);

        return mobilePageMapper.toDto(saved);
    }
}
