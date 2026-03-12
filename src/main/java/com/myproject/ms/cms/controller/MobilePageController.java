package com.myproject.ms.cms.controller;

import com.myproject.ms.cms.dto.MobilePageDto;
import com.myproject.ms.cms.repository.MobilePageFilter;
import com.myproject.ms.cms.service.MobilePageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/mobile/page")
public class MobilePageController {
    private final MobilePageService mobilePageService;

    public MobilePageController(MobilePageService mobilePageService) {
        this.mobilePageService = mobilePageService;
    }

    @GetMapping
    ResponseEntity<Page<MobilePageDto>> searchByQuery(MobilePageFilter filter, Pageable pageable) {
        return ResponseEntity.ok(mobilePageService.searchByQuery(filter, pageable));
    }

    @GetMapping("/{id}")
    ResponseEntity<MobilePageDto> getMobilePageById(@PathVariable String id){
        return ResponseEntity.ok(mobilePageService.findById(id));
    }

    @PostMapping
    ResponseEntity<MobilePageDto> createMobilePage(@RequestBody MobilePageDto mobilePageDto){
        return ResponseEntity.ok(mobilePageService.save(mobilePageDto));
    }
}
