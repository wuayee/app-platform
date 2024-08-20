/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.mapper;

import com.huawei.fit.jober.aipp.po.I18nPo;

import java.util.List;

/**
 * 国际化相关的数据库操作。
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
public interface I18nMapper {
    /**
     * 获取国际化资源。
     *
     * @return 表示国际化资源列表的 {@link List}{@code <}{@link I18nPo}{@code >}
     */
    List<I18nPo> selectResource();
}
