/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2024. All rights reserved.
 */

package com.huawei.fitframework.serialization.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.serialization.ByteSerializer;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.serialization.util.VaryingNumber;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.MapUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 为 {@link TagLengthValues} 提供默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2021-05-15
 */
public class DefaultTagLengthValues implements TagLengthValues {
    private static final byte[] EMPTY_VALUE = new byte[0];

    private final Map<Integer, byte[]> tagValues = new ConcurrentHashMap<>();

    @Override
    public Set<Integer> getTags() {
        return this.tagValues.keySet();
    }

    @Override
    public byte[] getValue(int tag) {
        return this.tagValues.getOrDefault(tag, EMPTY_VALUE);
    }

    @Override
    public void putTag(int tag, byte[] value) {
        Validation.notNull(value,
                "Cannot put null value to Tag-Length-Value, please use 'remove(int tag)' method. [tag={0}]",
                tag);
        this.tagValues.put(tag, value);
    }

    @Override
    public void putTags(Map<Integer, byte[]> tagValues) {
        if (MapUtils.isEmpty(tagValues)) {
            return;
        }
        tagValues.entrySet()
                .stream()
                .filter(entry -> entry.getKey() != null)
                .forEach(entry -> this.putTag(entry.getKey(), entry.getValue()));
    }

    @Override
    public void remove(int tag) {
        this.tagValues.remove(tag);
    }

    /**
     * 为 {@link TagLengthValues} 提供序列化程序。
     *
     * @author 邬涨财 w00575064
     * @since 2024-02-19
     */
    public static class Serializer implements ByteSerializer<TagLengthValues> {
        /** 获取序列化程序的唯一实例。 */
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void serialize(TagLengthValues tagValues, OutputStream out) throws IOException {
            for (Integer tag : tagValues.getTags()) {
                notNull(tag, "The tag cannot be null.");
                out.write(VaryingNumber.valueOf(tag).bytes());
                byte[] value = tagValues.getValue(tag);
                out.write(VaryingNumber.valueOf(value.length).bytes());
                out.write(value);
            }
        }

        @Override
        public TagLengthValues deserialize(InputStream in) throws IOException {
            TagLengthValues values = TagLengthValues.create();
            VaryingNumber tag;
            while ((tag = VaryingNumber.serializer().deserialize(in)) != null) {
                int length = VaryingNumber.serializer().deserialize(in).intValue();
                byte[] value = IoUtils.read(in, length);
                values.putTag(tag.intValue(), value);
            }
            return values;
        }
    }
}
