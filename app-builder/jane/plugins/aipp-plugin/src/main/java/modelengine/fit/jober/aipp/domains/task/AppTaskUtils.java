/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.PublishedAppResDto;

/**
 * {@link AppTask} 的工厂类
 *
 * @author 张越
 * @since 2025-02-06
 */
public class AppTaskUtils {
    /**
     * 转换为发布数据对象.
     *
     * @param appTask 任务领域对象.
     * @param appVersion 应用版本.
     * @return {@link PublishedAppResDto} 对象.
     */
    public static PublishedAppResDto toPublishedAppResDto(AppTask appTask, AppVersion appVersion) {
        return PublishedAppResDto.builder()
                .appId(appVersion.getData().getAppId())
                .appVersion(appVersion.getData().getVersion())
                .publishedAt(appTask.getEntity().getCreationTime())
                .publishedBy(appTask.getEntity().getCreator())
                .publishedDescription(appTask.getEntity().getPublishDescription())
                .publishedUpdateLog(appTask.getEntity().getPublishLog())
                .build();
    }
}
