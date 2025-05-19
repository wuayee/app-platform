/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import modelengine.fit.jober.aipp.converters.EntityConverter;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.AppBuilderAppMetadataDto;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * {@link AppVersion} -> {@link AppBuilderAppMetadataDto}.
 *
 * @author 张越
 * @since 2025-02-18
 */
@Component
public class AppVersionToMetaDtoConverter implements EntityConverter {
    @Override
    public Class<AppVersion> source() {
        return AppVersion.class;
    }

    @Override
    public Class<AppBuilderAppMetadataDto> target() {
        return AppBuilderAppMetadataDto.class;
    }

    @Override
    public AppBuilderAppMetadataDto convert(Object appVersion) {
        return Optional.ofNullable(appVersion).map(ObjectUtils::<AppVersion>cast).map(s -> {
            List<String> tags = new ArrayList<>();
            tags.add(s.getData().getType().toUpperCase(Locale.ROOT));
            return AppBuilderAppMetadataDto.builder()
                    .id(s.getData().getId())
                    .name(s.getData().getName())
                    .type(s.getData().getType())
                    .state(s.getData().getState())
                    .appType(s.getData().getAppType())
                    .attributes(s.getAttributes())
                    .version(s.getData().getVersion())
                    .createBy(s.getData().getCreateBy())
                    .updateBy(s.getData().getUpdateBy())
                    .createAt(s.getData().getCreateAt())
                    .updateAt(s.getData().getUpdateAt())
                    .appCategory(s.getData().getAppCategory())
                    .tags(tags)
                    .appBuiltType(s.getData().getAppBuiltType())
                    .build();
        }).orElse(null);
    }
}
