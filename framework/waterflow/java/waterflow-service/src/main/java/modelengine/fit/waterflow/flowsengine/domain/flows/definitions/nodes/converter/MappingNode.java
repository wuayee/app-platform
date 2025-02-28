/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 映射配置节点
 *
 * @author 宋永坦
 * @since 2024/4/18
 */
@AllArgsConstructor
@Builder
@Getter
public class MappingNode {
    private String name;
    private MappingNodeType type;
    private MappingFromType from;

    /**
     * from为reference时，表示对应节点下的参数key
     * from为value时，表示对应value的解析结构
     */
    private Object value;

    /**
     * 引用节点的id, from为reference时才有意义
     */
    private String referenceNode;

    /**
     * 数据查找是否走兜底查询，在businessData中进行二次查询
     */
    private boolean fallbackOnNodeDataMiss;

    public MappingNode(String name, MappingNodeType type, MappingFromType from, Object value, String referenceNode) {
        this.name = name;
        this.type = type;
        this.from = from;
        this.value = value;
        this.referenceNode = referenceNode;
        this.fallbackOnNodeDataMiss = false;
    }
}
