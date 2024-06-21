/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.langchain.retriever;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.jade.fel.core.retriever.Retriever;
import com.huawei.jade.fel.langchain.runnable.LangChainRunnable;
import com.huawei.jade.fel.retrieve.Document;
import com.huawei.jade.fel.retrieve.TextDocument;
import com.huawei.jade.fel.service.langchain.LangChainRunnableService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * LangChain 检索算子。
 *
 * @author 刘信宏
 * @since 2024-06-06
 */
public class LangChainRetriever implements Retriever<String, List<Document>> {
    private static final Logger log = Logger.get(LangChainRetriever.class);
    private static final String LANG_CHAIN_RETRIEVER_TASK = "langchain.retrieve";

    private final LangChainRunnable runnable;

    public LangChainRetriever(LangChainRunnableService runnableService, String fitableId) {
        this.runnable = new LangChainRunnable(runnableService, LANG_CHAIN_RETRIEVER_TASK, fitableId);
    }

    @Override
    public List<Document> invoke(String input) {
        Validation.notBlank(input, "The input data cannot be blank.");
        List<Map<String, Object>> res = this.castRetrieverOutput(runnable.invoke(input));
        return res.stream()
                .map(doc -> ObjectUtils.<TextDocument>cast(ObjectUtils.toCustomObject(doc, TextDocument.class)))
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> castRetrieverOutput(Object retrieverDocs) {
        if (retrieverDocs instanceof List<?>
                && ((List<?>) retrieverDocs).stream().allMatch(item -> item instanceof Map)) {
            return ObjectUtils.cast(retrieverDocs);
        }
        log.error("Invalid retriever output: {}", retrieverDocs.toString());
        throw new IllegalArgumentException("Invalid langchain retriever output.");
    }
}
