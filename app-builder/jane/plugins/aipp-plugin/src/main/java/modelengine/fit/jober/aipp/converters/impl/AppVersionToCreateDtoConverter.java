/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.converters.impl;

import modelengine.fit.jober.aipp.converters.EntityConverter;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Optional;

/**
 * {@link AppVersion} -> {@link AppBuilderAppCreateDto}.
 *
 * @author 张越
 * @since 2025-02-18
 */
@Component
public class AppVersionToCreateDtoConverter implements EntityConverter {
    @Override
    public Class<AppVersion> source() {
        return AppVersion.class;
    }

    @Override
    public Class<AppBuilderAppCreateDto> target() {
        return AppBuilderAppCreateDto.class;
    }

    @Override
    public AppBuilderAppCreateDto convert(Object appVersion) {
        return Optional.ofNullable(appVersion)
                .map(ObjectUtils::<AppVersion>cast)
                .map(s -> AppBuilderAppCreateDto.builder()
                        .name(s.getData().getName())
                        .description(s.getDescription())
                        .icon(s.getIcon())
                        .greeting(s.getGreeting())
                        .appType(s.getData().getAppType())
                        .type(s.getData().getType())
                        .storeId(s.getData().getUniqueName())
                        .appBuiltType(s.getData().getAppBuiltType())
                        .build())
                .orElse(null);
    }
}
