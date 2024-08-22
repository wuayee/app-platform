/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.retrieve;

import modelengine.fitframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 文本文档内容。
 *
 * @author 刘信宏
 * @since 2024-06-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDocument implements Document {
    private String content;
    private Map<String, Object> metadata;

    @Override
    public String text() {
        return Optional.ofNullable(this.content).orElse(StringUtils.EMPTY);
    }

    @Override
    public Map<String, Object> meta() {
        return Optional.ofNullable(this.metadata).orElseGet(Collections::emptyMap);
    }

    @Override
    public String toString() {
        return this.text();
    }
}
