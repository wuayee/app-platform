/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.enums;

/**
 * 流程实例节点状态
 * 状态流转顺序：NEW -> PENDING(停留在EVENT边上) -> READY(进入到节点) -> PROCESSING(开始处理) -> ARCHIVED(处理完成)
 *
 * @author 高诗意
 * @since 1.0
 */
public enum FlowNodeStatus {
    NEW,
    PENDING,
    READY, // 未更新数据库
    PROCESSING, // 未更新数据库
    ARCHIVED,
    TERMINATE,
    ERROR
}
