/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package modelengine.jade.oms.entity;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;

/**
 * 表示带名字的消息体数据。
 *
 * @author 季聿阶
 * @since 2022-10-12
 */
public class NamedEntity implements Entity {
    private final String name;
    private final Entity entity;

    /**
     * 创建带名字的消息体数据。
     *
     * @param name 表示消息体数据的名字的 {@link String}。
     * @param entity 表示消息体数据的 {@link Entity}。
     */
    public NamedEntity(String name, Entity entity) {
        this.name = ObjectUtils.nullIf(name, StringUtils.EMPTY);
        this.entity = notNull(entity, "The entity cannot be null.");
    }

    /**
     * 获取消息体数据的名字。
     *
     * @return 表示消息体数据的名字的 {@link String}。
     */
    public String name() {
        return this.name;
    }

    /**
     * 获取真正的消息体数据内容。
     *
     * @return 表示真正的消息体数据内容的 {@link Entity}。
     */
    public Entity entity() {
        return this.entity;
    }

    /**
     * 判断消息体数据是否是文件类型。
     *
     * @return 表示消息体数据是否是文件类型的 {@code boolean}。
     */
    public boolean isFile() {
        return this.entity instanceof FileEntity;
    }

    /**
     * 判断消息体数据是否是文本类型。
     *
     * @return 表示消息体数据是否是文本类型的 {@code boolean}。
     */
    public boolean isText() {
        return this.entity instanceof TextEntity;
    }

    /**
     * 将消息体数据转化成文件类型的消息体。
     *
     * @return 表示转化后的文件类型消息体的 {@link FileEntity}。
     */
    public FileEntity asFile() {
        return ObjectUtils.as(this.entity, FileEntity.class);
    }

    /**
     * 将消息体数据转化成文本类型的消息体。
     *
     * @return 表示转化后的文本类型消息体的 {@link TextEntity}。
     */
    public TextEntity asText() {
        return ObjectUtils.as(this.entity, TextEntity.class);
    }

    @Override
    @Nonnull
    public MimeType resolvedMimeType() {
        return this.entity.resolvedMimeType();
    }

    @Override
    public void close() throws IOException {
        this.entity.close();
    }
}
