/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.oms.entity;

import static modelengine.fitframework.util.ObjectUtils.getIfNull;

import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.inspection.Nonnull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 表示分块的消息体数据。
 *
 * @author 季聿阶
 * @since 2022-10-12
 */
public class PartitionedEntity implements Entity {
    private final List<NamedEntity> namedEntities;

    /**
     * 创建分块的消息体数据对象。
     *
     * @param namedEntities 表示带名字的消息体数据列表的 {@link List}{@code <}{@link NamedEntity}{@code >}。
     */
    public PartitionedEntity(List<NamedEntity> namedEntities) {
        this.namedEntities = getIfNull(namedEntities, Collections::emptyList);
    }

    /**
     * 获取数据列表。
     *
     * @return 返回消息体列表的 {@link List}{@code <}{@link NamedEntity}{@code >}。
     */
    public List<NamedEntity> entities() {
        return Collections.unmodifiableList(this.namedEntities);
    }

    @Override
    @Nonnull
    public MimeType resolvedMimeType() {
        return MimeType.MULTIPART_FORM_DATA;
    }

    @Override
    public void close() throws IOException {
        for (NamedEntity entity : this.namedEntities) {
            if (entity != null) {
                entity.close();
            }
        }
    }
}
