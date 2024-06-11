/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface.type;

import com.huawei.fitframework.resource.web.Media;
import com.huawei.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 表示 huggingface pipeline 的常量集合。
 *
 * @author 易文渊
 * @since 2024-06-06
 */
public interface Constant {
    /**
     * 表示 {@link List}{@code <}{@link Media}{@code >} 的 {@link Type}。
     */
    Type LIST_MEDIA_TYPE = TypeUtils.parameterized(List.class, new Type[] {Media.class});
}