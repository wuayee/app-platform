/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.parsers.nodes.jobers;

import modelengine.fit.waterflow.domain.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.domain.definitions.nodes.jobers.FlowOhScriptJober;
import modelengine.fit.waterflow.domain.enums.FlowJoberType;
import modelengine.fit.waterflow.domain.parsers.FlowGraphData;

/**
 * ohScript脚本任务解析器
 *
 * @author 杨祥宇
 * @since 1.0
 */
public class OhScriptJoberParser implements JoberParser {
    @Override
    public FlowJober parseJober(FlowGraphData flowGraphData, int nodeIndex) {
        FlowJober flowJober = new FlowOhScriptJober();
        flowJober.setType(FlowJoberType.OHSCRIPT_JOBER);
        commonParse(flowJober, flowGraphData, nodeIndex);
        flowJober.setFitables(flowGraphData.getNodeJoberFitables(nodeIndex));
        return flowJober;
    }
}
