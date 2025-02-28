/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.command;

import lombok.Getter;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 表示http调用的返回结果。
 *
 * @author 张越
 * @since 2024-12-23
 */
@Getter
public class HttpCallResult {
    private Integer status;
    private String errorMsg;
    private Object data;

    public HttpCallResult(HttpClassicClientResponse<Object> response) {
        this.load(response);
    }

    public HttpCallResult(Integer status, String errorMsg) {
        this.status = status;
        this.errorMsg = errorMsg;
    }

    private void load(HttpClassicClientResponse<Object> response) {
        this.status = response.statusCode();
        response.entity().map(this::parseEntity).ifPresent(d -> {
            if (this.status >= 200 && this.status < 300) {
                this.data = d;
            } else {
                this.errorMsg = d.toString();
            }
        });
    }

    private Object parseEntity(Entity entity) {
        MimeType mimeType = entity.resolvedMimeType();
        switch (mimeType) {
            case TEXT_PLAIN: {
                TextEntity textEntity = ObjectUtils.cast(entity);
                return textEntity.content();
            }
            case APPLICATION_JSON: {
                ObjectEntity<Object> objectEntity = ObjectUtils.cast(entity);
                return objectEntity.object();
            }
            default:
                throw new IllegalArgumentException("Unsupported mime type: " + mimeType);
        }
    }
}
