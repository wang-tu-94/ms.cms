package com.myproject.ms.cms.controller;

import com.myproject.ms.cms.config.JwtAuthenticationFilter;
import com.myproject.ms.cms.dto.MobilePageDto;
import com.myproject.ms.cms.exception.NotFoundException;
import com.myproject.ms.cms.model.State;
import com.myproject.ms.cms.repository.MobilePageFilter;
import com.myproject.ms.cms.service.MobilePageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MobilePageController.class)
class MobilePageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MobilePageService mobilePageService;

    @Test
    @WithMockUser
    @DisplayName("GET /v1/mobile/page should return a page of DTOs")
    void shouldSearchByQuery() throws Exception {
        MobilePageDto dto = new MobilePageDto("id1", State.OFFLINE, "Test Page", null, null, List.of());
        PageImpl<MobilePageDto> page = new PageImpl<>(List.of(dto));

        when(mobilePageService.searchByQuery(any(MobilePageFilter.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/v1/mobile/page")
                        .param("name", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value("id1"))
                .andExpect(jsonPath("$.content[0].name").value("Test Page"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /v1/mobile/page/{id} should return 200 when found")
    void shouldGetById() throws Exception {
        String id = "65ef1234567890";
        MobilePageDto dto = new MobilePageDto(id, State.ONLINE, "Found", null, null, List.of());

        when(mobilePageService.findById(id)).thenReturn(dto);

        mockMvc.perform(get("/v1/mobile/page/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Found"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /v1/mobile/page should create and return the page")
    void shouldCreatePage() throws Exception {
        MobilePageDto inputDto = new MobilePageDto(null, State.OFFLINE, "New Page", null, null, List.of());
        MobilePageDto outputDto = new MobilePageDto("generated-id", State.OFFLINE, "New Page", Instant.now(), null, List.of());

        when(mobilePageService.save(any(MobilePageDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/v1/mobile/page")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("generated-id"))
                .andExpect(jsonPath("$.name").value("New Page"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /v1/mobile/page/{id} should return 404 or 500 depending on service exception")
    void shouldHandleNotFound() throws Exception {
        String id = "non-existent";
        when(mobilePageService.findById(id)).thenThrow(new NotFoundException("Page not found"));

        mockMvc.perform(get("/v1/mobile/page/{id}", id))
                .andExpect(status().isNotFound());
    }
}