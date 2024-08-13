/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 常数类
 *
 * @author 晏钰坤
 * @since 2023/6/15
 */
public final class Constant {
    /**
     * 流程定义ID分隔符
     */
    public static final char STREAM_ID_SEPARATOR = '-';

    /**
     * 默认支持语言
     */
    public static final List<Locale> LOCALES = Collections.unmodifiableList(
            Arrays.asList(new Locale("en"), new Locale("zh"), new Locale("en", "US"), new Locale("zh", "CN")));
}
