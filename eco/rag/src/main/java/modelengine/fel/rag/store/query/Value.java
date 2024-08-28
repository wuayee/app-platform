/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.store.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 表达式中的值。
 *
 * @since 2024-05-07
 */
@Getter
@AllArgsConstructor
public class Value implements Element {
    private Object value;
}
