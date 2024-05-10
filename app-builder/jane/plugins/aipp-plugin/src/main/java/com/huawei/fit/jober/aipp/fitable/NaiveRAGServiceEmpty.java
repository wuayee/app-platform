/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jade.NaiveRAGService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.StringUtils;

import java.util.List;

/**
 * 临时屏蔽rag代码
 *
 * @author 黄夏露 h00804153
 * @since 2024-05-10
 */
@Component
public class NaiveRAGServiceEmpty implements NaiveRAGService {
    @Override
    public String process(Integer topK, List<String> collectionName, String question) {
        return StringUtils.EMPTY;
    }
}
