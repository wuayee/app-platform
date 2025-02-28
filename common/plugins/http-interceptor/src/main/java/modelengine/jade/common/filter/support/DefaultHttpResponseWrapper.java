/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.server.ErrorResponse;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.common.filter.config.DefaultHttpResponseWrapperConfig;
import modelengine.jade.common.vo.Result;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 默认 http 响应包装器。
 * <p>
 * 将处理结果包装成 {@link Result}，注意以下情况：
 * <ul>
 *     <li>返回值为 {@link ErrorResponse} 不进行包装，返回此对象代表异常未被全局处理器捕获，需要透出。</li>
 * </ul>
 * </p>
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@Component
public class DefaultHttpResponseWrapper implements HttpServerFilter {
    private final List<String> matchPatterns;
    private final List<String> mismatchPatterns;

    /**
     * 根据配置创建包装器的实例。
     *
     * @param config 表示包装器配置的 {@link DefaultHttpResponseWrapperConfig}。
     */
    public DefaultHttpResponseWrapper(DefaultHttpResponseWrapperConfig config) {
        this.matchPatterns = ObjectUtils.nullIf(config.getSupport(), Collections.emptyList());
        this.mismatchPatterns = ObjectUtils.nullIf(config.getNonsupport(), Collections.emptyList());
    }

    @Override
    public String name() {
        return "DefaultHttpResponseWrapper";
    }

    @Override
    public int priority() {
        return Order.LOWEST;
    }

    @Override
    public List<String> matchPatterns() {
        return this.matchPatterns;
    }

    @Override
    public List<String> mismatchPatterns() {
        return this.mismatchPatterns;
    }

    @Override
    public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
            HttpServerFilterChain chain) {
        chain.doFilter(request, response);
        Optional<Entity> entityOptional = response.entity();
        // 处理 void
        if (!entityOptional.isPresent()) {
            response.entity(ObjectEntity.create(response, Result.ok()));
            return;
        }
        Entity entity = entityOptional.get();
        // 处理 string
        if (entity instanceof TextEntity) {
            response.entity(ObjectEntity.create(response, Result.ok(((TextEntity) entity).content())));
            return;
        }
        if (!(entity instanceof ObjectEntity)) {
            return;
        }
        Object object = ((ObjectEntity<?>) entity).object();
        if (object instanceof Result || object instanceof ErrorResponse) {
            return;
        }
        // 处理基本类型或者自定义类型
        response.entity(ObjectEntity.create(response, Result.ok(object)));
    }

    @Override
    public Scope scope() {
        return Scope.GLOBAL;
    }
}