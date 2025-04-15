/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.repository;

import java.util.Map;

/**
 * 国际化相关数据操作对象。
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
public interface I18nRepository {
    /**
     * 获取翻译表的所有内容。
     *
     * @return 表示翻译表的所有内容的 {@link Map}{@code <}{@link String},
     * {@link Map}{@code <}{@link String}, {@link String}{@code >}{@code >}。
     */
    Map<String, Map<String, String>> selectResource();
}
