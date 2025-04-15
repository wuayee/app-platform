/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.dto.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用导出配置的流程图。
 *
 * @author 方誉州
 * @since 2024-10-30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppExportFlowGraph {
    private String name;
    private String appearance;
}
