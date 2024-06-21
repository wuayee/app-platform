/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.knowledge.dto.KbVectorSearchDto;
import com.huawei.jade.app.engine.knowledge.service.KnowledgeBaseService;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * naive RAG 组件实现
 *
 * @author h00804153
 * @since 2024-04-15
 */
@Component
public class NaiveRAGComponent implements FlowableService {
    private static final Logger log = Logger.get(NaiveRAGComponent.class);
    private final KnowledgeBaseService knowledgeBaseService;
    public NaiveRAGComponent(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    /**
     * 根据知识库生成检索结果的实现
     *
     * @param flowData 流程执行上下文数据，包含用户选中的知识库列表
     * @return 流程执行上下文数据，包含生成的检索结果
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.NaiveRAGComponent")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = Utils.getBusiness(flowData);
        log.debug("NaiveRAGComponent business data {}", businessData);
        String ragOutput = StringUtils.EMPTY;
        List<Map<String, Object>> knowledgeList = ObjectUtils.cast(businessData.get("knowledge"));
        List<Long> tableIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(knowledgeList)) {
            tableIdList = knowledgeList.stream()
                    .filter(MapUtils::isNotEmpty)
                    .map(map -> Long.valueOf(ObjectUtils.cast(map.get("tableId"))))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(tableIdList)) {
            KbVectorSearchDto kbVectorSearchCondition = this.buildKbVectorSearchDto(tableIdList, businessData);
            List<String> chunksList = this.knowledgeBaseService.vectorSearchKnowledgeTable(kbVectorSearchCondition);
            ragOutput = String.join("; ", chunksList);
        }
        businessData.putIfAbsent("output", new HashMap<String, Object>());
        Map<String, Object> output = ObjectUtils.cast(businessData.get("output"));
        output.put("retrievalOutput", ragOutput);
        return flowData;
    }

    private KbVectorSearchDto buildKbVectorSearchDto(List<Long> tableIdList, Map<String, Object> businessData) {
        KbVectorSearchDto kbVectorSearchCondition = new KbVectorSearchDto();
        kbVectorSearchCondition.setTableId(tableIdList);
        kbVectorSearchCondition.setContent(ObjectUtils.cast(businessData.get("query")));
        kbVectorSearchCondition.setTopK(ObjectUtils.cast(businessData.get("maximum")));
        kbVectorSearchCondition.setThreshold(0.5F);
        return kbVectorSearchCondition;
    }
}
