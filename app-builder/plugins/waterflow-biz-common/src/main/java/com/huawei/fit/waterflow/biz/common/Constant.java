/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.common;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 常数类
 *
 * @author 00693950
 * @since 2023/6/15
 */
public final class Constant {
    /**
     * 流程定义ID分隔符
     */
    public static final char STREAM_ID_SEPARATOR = '-';

    /**
     * 表示 URI 的前缀，包含 tenant_id 路径参数。
     */
    public static final String BASE_URI_PREFIX = "/v1/{tenant_id}";

    /**
     * tianzhou url前缀
     */
    public static final String TIANZHOU_URL_PREFIX = "/v1/{tenant_id}/jane";

    /**
     * 默认支持语言
     */
    public static final List<Locale> LOCALES = Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"),
            new Locale("zh", "CN"));
}
