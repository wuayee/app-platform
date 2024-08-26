/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects;

import lombok.Getter;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors.ValueExtractor;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 编排 node 或 event 的属性.
 *
 * @author 张越
 * @since 2024-08-05
 */
public class Attribute {
    @Getter
    private final String key;
    private List<String> path;
    private ValueExtractor extractor;

    @Getter
    private Object value;

    /**
     * 构造函数.
     *
     * @param key 键值.
     * @param path 提取路径.
     */
    public Attribute(String key, List<String> path) {
        this.key = key;
        this.path = path;
    }

    /**
     * 构造函数.
     *
     * @param key 键值.
     * @param extractor 提取器.
     */
    public Attribute(String key, ValueExtractor extractor) {
        this.key = key;
        this.extractor = extractor;
    }

    /**
     * 构造函数.
     *
     * @param key 键值.
     * @param value 真实值.
     */
    public Attribute(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    /**
     * 提取值.
     *
     * @param data 数据.
     */
    public void extract(AttributesData data) {
        if (!Objects.isNull(this.value)) {
            return;
        }
        this.value = Optional.ofNullable(this.path)
                .filter(CollectionUtils::isNotEmpty)
                .map(p -> this.extractByPath(data))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElseGet(() -> this.extractByExtractor(data));
    }

    private Object extractByExtractor(AttributesData data) {
        return Optional.ofNullable(this.extractor).map(e -> e.extract(data)).orElse(null);
    }

    private Optional<Object> extractByPath(AttributesData data) {
        Map<String, Object> tmp = data.getData();
        for (int i = 0; i < this.path.size(); i++) {
            String p = this.path.get(i);
            if (i == this.path.size() - 1) {
                return Optional.ofNullable(ObjectUtils.cast(tmp.get(p)));
            } else {
                tmp = ObjectUtils.cast(tmp.get(p));
                if (Objects.isNull(tmp)) {
                    break;
                }
            }
        }
        return Optional.empty();
    }
}
