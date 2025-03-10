/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter.testcase.entity;

import lombok.NoArgsConstructor;
import modelengine.fit.jade.aipp.formatter.ItemType;
import modelengine.fit.jade.aipp.formatter.MessageItem;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 可序列化的应用响应数据。
 *
 * @author 刘信宏
 * @since 2024-11-22
 */
@NoArgsConstructor
public class FlatMessageItem implements MessageItem {
    private String type;
    private String data;
    private LinkedHashMap<String, Object> reference;

    @Nonnull
    @Override
    public ItemType type() {
        return ItemType.from(ObjectUtils.nullIf(this.type, StringUtils.EMPTY));
    }

    @Nonnull
    @Override
    public String data() {
        return ObjectUtils.nullIf(this.data, StringUtils.EMPTY);
    }

    @Nonnull
    @Override
    public Map<String, Object> reference() {
        return ObjectUtils.nullIf(this.reference, Collections.emptyMap());
    }
}
