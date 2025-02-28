/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 应用导出配置中的应用信息配置。
 *
 * @author 方誉州
 * @since 2024-10-30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppExportApp {
    private String name;

    private String tenantId;

    private String type;

    private String appBuiltType;

    private String version;

    private Map<String, Object> attributes;

    private String appCategory;

    private String appType;
}
