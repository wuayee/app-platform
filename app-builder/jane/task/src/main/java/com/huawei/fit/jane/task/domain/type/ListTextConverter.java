/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.task.domain.type;

/**
 * 为文本列表提供数据转换器。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-24
 */
public class ListTextConverter extends AbstractListDataConverter {
    /**
     * 获取当前类型的唯一实例。
     */
    public static final ListTextConverter INSTANCE = new ListTextConverter();

    private ListTextConverter() {
        super(TextConverter.INSTANCE);
    }
}
