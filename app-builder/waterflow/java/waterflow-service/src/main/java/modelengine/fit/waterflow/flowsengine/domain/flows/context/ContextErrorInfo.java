/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * flow context错误信息类
 *
 * @author 杨祥宇
 * @since 2023/12/19
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContextErrorInfo {
    /**
     * 错误码
     */
    private Integer errorCode;

    /**
     * 错误详细信息说明
     */
    private String errorMessage;

    /**
     * 错误信息对应参数
     */
    private String[] args;

    /**
     * 调用出错的fitable实现
     * 后续去除
     *
     */
    private String fitableId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 错误的其他信息
     * fitableId:调用的插件
     * toolId:工具Id
     */
    private Map<String, String> properties;
}
