/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package modelengine.fit.http.entity.support;

import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link PartitionedEntity} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-10-12
 */
public class DefaultPartitionedEntity extends AbstractEntity implements PartitionedEntity {
    private final List<NamedEntity> namedEntities;

    /**
     * 创建分块的消息体数据对象。
     *
     * @param httpMessage 表示消息体数据所属的 Http 消息的 {@link HttpMessage}。
     * @param namedEntities 表示带名字的消息体数据列表的 {@link List}{@code <}{@link NamedEntity}{@code >}。
     */
    public DefaultPartitionedEntity(HttpMessage httpMessage, List<NamedEntity> namedEntities) {
        super(httpMessage);
        this.namedEntities = getIfNull(namedEntities, Collections::emptyList);
    }

    @Override
    public List<NamedEntity> entities() {
        return Collections.unmodifiableList(this.namedEntities);
    }

    @Nonnull
    @Override
    public MimeType resolvedMimeType() {
        return MimeType.MULTIPART_FORM_DATA;
    }

    @Override
    public void close() throws IOException {
        super.close();
        for (NamedEntity entity : this.namedEntities) {
            if (entity != null) {
                entity.close();
            }
        }
    }
}
