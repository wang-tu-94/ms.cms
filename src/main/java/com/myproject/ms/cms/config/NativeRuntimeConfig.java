package com.myproject.ms.cms.config;

import com.myproject.ms.cms.dto.MobilePageDto;
import com.myproject.ms.cms.model.MobilePage;
import com.myproject.ms.cms.model.block.*;
import com.myproject.ms.cms.repository.MobilePageFilter;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding({
        MobilePage.class,
        MobilePageDto.class,
        TextBlock.class,
        ImageBlock.class,
        EventBlock.class,
        ContentListBlock.class,
        Block.class
})
public class NativeRuntimeConfig {
}
