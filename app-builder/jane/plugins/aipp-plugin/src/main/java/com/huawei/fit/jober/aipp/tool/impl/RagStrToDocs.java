/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool.impl;

import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.tool.RagTool;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.retrieve.TextDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link RagTool} 的实现。
 *
 * @author 刘信宏
 * @since 2024-06-26
 */
@Component
public class RagStrToDocs implements RagTool {
    @Fitable("naive.rag.StrToDocs")
    @Override
    public List<TextDocument> contentToDocs(String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        ArrayList<String> splitTexts = StringUtils.split(content, AippConst.CONTENT_DELIMITER, ArrayList::new);
        return splitTexts.stream()
                .map(text -> new TextDocument(text, MapBuilder.<String, Object>get().put("score", 0.5).build()))
                .collect(Collectors.toList());
    }
}
