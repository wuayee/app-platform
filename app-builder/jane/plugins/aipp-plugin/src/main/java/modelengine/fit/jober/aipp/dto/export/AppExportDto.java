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
import modelengine.fitframework.annotation.Property;

/**
 * 应用导出配置类。
 *
 * @author 方誉州
 * @since 2024-10-30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppExportDto {
    @Property(description = "应用配置版本号")
    String version;

    @Property(description = "应用基础信息")
    AppExportApp app;

    @Property(description = "应用 configUI 配置信息")
    AppExportConfig config;

    @Property(description = "应用流程图配置信息")
    AppExportFlowGraph flowGraph;
}
