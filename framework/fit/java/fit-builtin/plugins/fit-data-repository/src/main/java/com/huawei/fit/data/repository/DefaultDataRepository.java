/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository;

import static com.huawei.fitframework.inspection.Validation.notBlank;

import com.huawei.fit.data.repository.entity.Metadata;
import com.huawei.fit.data.repository.entity.MetadataType;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示数据仓库的本地进程缓存。
 *
 * @author 季聿阶 j00559309
 * @since 2024-01-21
 */
@Component
public class DefaultDataRepository implements DataRepository {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    @Fitable(id = "local-worker")
    public void save(String id, Object data) {
        notBlank(id, "The cache id to save cannot be blank.");
        this.cache.put(id, data);
    }

    @Override
    @Fitable(id = "local-worker")
    public void delete(String id) {
        notBlank(id, "The cache id to remove cannot be blank.");
        this.cache.remove(id);
    }

    @Override
    @Fitable(id = "local-worker")
    public Metadata getMetadata(String id) {
        Object data = this.get(id);
        Metadata metadata = new Metadata();
        if (data == null) {
            throw new IllegalStateException(StringUtils.format("The cache data cannot be null. [cacheId={0}]", id));
        } else if (data instanceof byte[]) {
            metadata.setType(MetadataType.BYTES.code());
            metadata.setLength(ObjectUtils.<byte[]>cast(data).length);
        } else if (data instanceof String) {
            metadata.setType(MetadataType.STRING.code());
            metadata.setLength(ObjectUtils.<String>cast(data).length() * 2);
        } else {
            throw new IllegalStateException(StringUtils.format("Not supported data type to get. [type={0}]",
                    data.getClass().getName()));
        }
        return metadata;
    }

    @Override
    @Fitable(id = "local-worker")
    public String getString(String id) {
        return ObjectUtils.cast(this.get(id));
    }

    @Override
    @Fitable(id = "local-worker")
    public byte[] getBytes(String id) {
        return ObjectUtils.cast(this.get(id));
    }

    private Object get(String id) {
        notBlank(id, "The cache id to get cannot be blank.");
        return this.cache.get(id);
    }
}
