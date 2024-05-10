/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jade.NaiveRAGService;
import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
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
    private static final String KNOWLEDGE_NAME_PREFIX = "KnowledgeBase_";
    private final NaiveRAGService naiveRAGService;
    public NaiveRAGComponent(NaiveRAGService naiveRAGService) {
        this.naiveRAGService = naiveRAGService;
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
        List<String> collectionNameList = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(knowledgeList)) {
            collectionNameList = knowledgeList.stream()
                    .filter(MapUtils::isNotEmpty)
                    .map(map -> KNOWLEDGE_NAME_PREFIX + map.get("id"))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(collectionNameList)) {
            String query = ObjectUtils.cast(businessData.get("query"));
            Integer maximum = ObjectUtils.cast(businessData.get("maximum"));
            ragOutput = this.naiveRAGService.process(maximum, collectionNameList, query);
        }
        if (ragOutput == null) {
            log.error("Connect naiveRAGService failed!");
            throw new AippException(Utils.getOpContext(businessData), AippErrCode.UNKNOWN);
        }
        businessData.putIfAbsent("output", new HashMap<String, Object>());
        Map<String, Object> output = ObjectUtils.cast(businessData.get("output"));
        output.put("retrievalOutput", ragOutput);
        return flowData;
    }
}
