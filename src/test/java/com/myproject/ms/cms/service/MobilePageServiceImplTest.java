package com.myproject.ms.cms.service;

import com.myproject.ms.cms.dto.MobilePageDto;
import com.myproject.ms.cms.exception.BadRequestException;
import com.myproject.ms.cms.exception.NotFoundException;
import com.myproject.ms.cms.mapper.MobilePageMapper;
import com.myproject.ms.cms.model.MobilePage;
import com.myproject.ms.cms.model.State;
import com.myproject.ms.cms.model.block.Block;
import com.myproject.ms.cms.repository.MobilePageFilter;
import com.myproject.ms.cms.repository.MobilePageRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MobilePageServiceImplTest {
    private final MobilePageRepository mobilePageRepository = Mockito.mock(MobilePageRepository.class);

    private final MobilePageMapper mobilePageMapper = Mockito.mock(MobilePageMapper.class);

    private final MobilePageServiceImpl mobilePageService = new MobilePageServiceImpl(mobilePageRepository, mobilePageMapper);

    @Test
    @DisplayName("Should return paginated DTOs when searching by query")
    void shouldSearchByQuery() {
        MobilePageFilter filter = new MobilePageFilter("promo", State.ONLINE, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        Instant now = Instant.now();
        List<Block> blocks = List.of();

        MobilePage page1 = new MobilePage();
        page1.setId(new ObjectId());
        page1.setName("promo 1");
        page1.setState(State.ONLINE);
        page1.setPublishedDate(now);
        page1.setUpdatedDate(now);
        page1.setBlocks(blocks);

        Page<MobilePage> entityPage = new PageImpl<>(List.of(page1));

        MobilePageDto dto1 = new MobilePageDto(
                page1.getId().toHexString(),
                State.ONLINE,
                "promo 1",
                now,
                now,
                blocks
        );

        when(mobilePageRepository.searchByFilter(filter, pageable)).thenReturn(entityPage);
        when(mobilePageMapper.toDto(page1)).thenReturn(dto1);

        Page<MobilePageDto> result = mobilePageService.searchByQuery(filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("promo 1");
        assertThat(result.getContent().getFirst().state()).isEqualTo(State.ONLINE);

        verify(mobilePageRepository).searchByFilter(filter, pageable);
        verify(mobilePageMapper).toDto(page1);
    }

    @Test
    @DisplayName("Should return DTO when page is found by ID")
    void shouldFindByIdWhenExists() {
        // GIVEN
        String stringId = new ObjectId().toHexString();
        ObjectId objectId = new ObjectId(stringId);
        Instant now = Instant.now();

        MobilePage entity = new MobilePage();
        entity.setId(objectId);
        entity.setName("Found Page");

        MobilePageDto expectedDto = new MobilePageDto(
                stringId, State.OFFLINE, "Found Page", now, null, List.of()
        );

        when(mobilePageRepository.findById(objectId)).thenReturn(Optional.of(entity));
        when(mobilePageMapper.toDto(entity)).thenReturn(expectedDto);

        // WHEN
        MobilePageDto result = mobilePageService.findById(stringId);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(stringId);
        assertThat(result.name()).isEqualTo("Found Page");
        verify(mobilePageRepository).findById(objectId);
    }

    @Test
    @DisplayName("Should throw Exception when page is not found by ID")
    void shouldThrowExceptionWhenFindByIdNotFound() {
        // GIVEN
        String stringId = new ObjectId().toHexString();
        ObjectId objectId = new ObjectId(stringId);

        when(mobilePageRepository.findById(objectId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> mobilePageService.findById(stringId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Page introuvable avec l'ID");
    }

    @Test
    @DisplayName("Should throw Exception when ID format is invalid")
    void shouldThrowExceptionWhenIdIsInvalid() {
        // GIVEN
        String invalidId = "invalid-hex";

        // WHEN & THEN
        assertThatThrownBy(() -> mobilePageService.findById(invalidId))
                .isInstanceOf(BadRequestException.class);

        verify(mobilePageRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should save a new MobilePage and return DTO with generated ID")
    void shouldSaveNewMobilePage() {
        Instant now = Instant.now();
        List<Block> blocks = List.of();

        MobilePageDto inputDto = new MobilePageDto(
                null, State.OFFLINE, "Nouvelle Page", null, null, blocks
        );

        MobilePage entityToSave = new MobilePage();
        entityToSave.setName("Nouvelle Page");
        entityToSave.setState(State.OFFLINE);
        entityToSave.setBlocks(blocks);

        MobilePage savedEntity = new MobilePage();
        ObjectId generatedId = new ObjectId();
        savedEntity.setId(generatedId);
        savedEntity.setName("Nouvelle Page");
        savedEntity.setState(State.OFFLINE);
        savedEntity.setBlocks(blocks);
        savedEntity.setCreatedDate(now);
        savedEntity.setUpdatedDate(now);

        MobilePageDto expectedOutputDto = new MobilePageDto(
                generatedId.toHexString(), State.OFFLINE, "Nouvelle Page", now, null, blocks
        );

        when(mobilePageMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(mobilePageRepository.save(entityToSave)).thenReturn(savedEntity);
        when(mobilePageMapper.toDto(savedEntity)).thenReturn(expectedOutputDto);

        // WHEN
        MobilePageDto result = mobilePageService.save(inputDto);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(generatedId.toHexString());
        assertThat(result.name()).isEqualTo("Nouvelle Page");

        verify(mobilePageMapper).toEntity(inputDto);
        verify(mobilePageRepository).save(entityToSave);
        verify(mobilePageMapper).toDto(savedEntity);
    }

    @Test
    @DisplayName("Should update an existing MobilePage (full replace)")
    void shouldUpdateExistingMobilePage() {
        String existingId = new ObjectId().toHexString();
        Instant now = Instant.now();
        List<Block> blocks = List.of();

        MobilePageDto inputDto = new MobilePageDto(
                existingId, State.ONLINE, "Page Mise à jour", now, now, blocks
        );

        MobilePage entityToUpdate = new MobilePage();
        entityToUpdate.setId(new ObjectId(existingId));
        entityToUpdate.setName("Page Mise à jour");
        entityToUpdate.setState(State.ONLINE);

        MobilePageDto expectedOutputDto = new MobilePageDto(
                existingId, State.ONLINE, "Page Mise à jour", now, now, blocks
        );

        when(mobilePageMapper.toEntity(inputDto)).thenReturn(entityToUpdate);
        when(mobilePageRepository.save(entityToUpdate)).thenReturn(entityToUpdate);
        when(mobilePageMapper.toDto(entityToUpdate)).thenReturn(expectedOutputDto);

        MobilePageDto result = mobilePageService.save(inputDto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(existingId);
        assertThat(result.name()).isEqualTo("Page Mise à jour");

        verify(mobilePageRepository).save(entityToUpdate);
    }
}