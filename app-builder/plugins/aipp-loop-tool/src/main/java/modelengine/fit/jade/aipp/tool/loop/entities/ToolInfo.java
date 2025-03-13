/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.tool.loop.entities;

import lombok.Data;

import java.util.List;

/**
 * 实际需要循环展开的工具信息
 *
 * @author 夏斐
 * @since 2025/3/10
 */
@Data
public class ToolInfo {
    private String uniqueName;

    private List<ParamInfo> params;

    /**
     * 参数信息
     *
     * @author 夏斐
     * @since 2025/3/10
     */
    @Data
    public static class ParamInfo {
        private String name;
    }
}
