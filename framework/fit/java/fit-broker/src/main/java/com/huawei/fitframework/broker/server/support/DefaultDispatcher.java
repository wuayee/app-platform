/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.server.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.broker.LocalExecutor;
import com.huawei.fitframework.broker.LocalExecutorFactory;
import com.huawei.fitframework.broker.UniqueFitableId;
import com.huawei.fitframework.broker.server.Dispatcher;
import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.broker.server.ServerLocalExecutorNotFoundException;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.exception.MethodInvocationException;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.RequestMetadata;
import com.huawei.fitframework.serialization.ResponseMetadataV2;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.serialization.tlv.ValueSerializer;
import com.huawei.fitframework.serialization.tlv.support.ExceptionPropertiesValueSerializer;
import com.huawei.fitframework.util.ExceptionUtils;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 表示 {@link Dispatcher} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2022-10-17
 */
public class DefaultDispatcher implements Dispatcher {
    private static final Logger log = Logger.get(DefaultDispatcher.class);

    private final LazyLoader<LocalExecutorFactory> localExecutorFactoryLoader;

    /**
     * 创建默认的请求转发器。
     *
     * @param container 表示 Bean 容器的 {@link BeanContainer}。
     */
    public DefaultDispatcher(BeanContainer container) {
        notNull(container, "The bean container cannot be null.");
        this.localExecutorFactoryLoader = new LazyLoader<>(() -> container.factory(LocalExecutorFactory.class)
                .map(BeanFactory::<LocalExecutorFactory>get)
                .orElseThrow(() -> new IllegalStateException("No local executor factory.")));
    }

    @Override
    public Response dispatch(RequestMetadata metadata, Object[] data) {
        try {
            this.deserializeTlv(metadata.tagValues());
            LocalExecutor executor = this.getLocalExecutor(metadata);
            Object response = executor.execute(data);
            log.debug("Execute fitable successfully. [genericableId={}, fitableId={}]",
                    metadata.genericableId(),
                    metadata.fitableId());
            return Response.create(this.successfulResponseMetadataBytes(metadata.dataFormat()),
                    executor.method().getGenericReturnType(),
                    response);
        } catch (Throwable cause) {
            log.error("Failed to execute fitable. [genericableId={}, fitableId={}, cause={}]",
                    metadata.genericableId(),
                    metadata.fitableId(),
                    cause.getMessage(),
                    cause);
            return Response.create(this.getResponseMetadata(cause, metadata.dataFormat()), null, new byte[0]);
        } finally {
            log.debug("Prepare to clear global context.");
        }
    }

    private LocalExecutor getLocalExecutor(RequestMetadata reqMetadata) {
        return this.localExecutorFactoryLoader.get()
                .get(UniqueFitableId.create(reqMetadata.genericableId(), reqMetadata.fitableId()))
                .orElseThrow(() -> new ServerLocalExecutorNotFoundException("No local executor."));
    }

    private void deserializeTlv(TagLengthValues tagLengthValues) {
        // 暂时不对TLV进行反序列化
    }

    private ResponseMetadataV2 getResponseMetadata(Throwable cause, int format) {
        if (cause instanceof MethodInvocationException) {
            Throwable actualCause = ExceptionUtils.getActualCause((MethodInvocationException) cause);
            return this.handleException(actualCause, format);
        } else {
            return this.handleException(cause, format);
        }
    }

    private ResponseMetadataV2 handleException(Throwable cause, int format) {
        if (cause instanceof FitException) {
            FitException actualCause = ObjectUtils.cast(cause);
            TagLengthValues tagLengthValues = this.buildTagLengthValues(actualCause.getProperties());
            return this.responseMetadataBytes(format, actualCause.getCode(), actualCause.getMessage(), tagLengthValues);
        }
        return this.responseMetadataBytes(format, -1, cause.getMessage(), null);
    }

    private TagLengthValues buildTagLengthValues(Map<String, String> properties) {
        TagLengthValues tagLengthValues = TagLengthValues.create();
        tagLengthValues.putTag(ValueSerializer.TAG_EXCEPTION_PROPERTIES,
                ExceptionPropertiesValueSerializer.INSTANCE.serialize(properties));
        return tagLengthValues;
    }

    private ResponseMetadataV2 successfulResponseMetadataBytes(int format) {
        return this.responseMetadataBytes(format, 0, StringUtils.EMPTY, null);
    }

    private ResponseMetadataV2 responseMetadataBytes(int format, int code, String message,
            TagLengthValues tagLengthValues) {
        return ResponseMetadataV2.custom()
                .version(ResponseMetadataV2.CURRENT_VERSION)
                .dataFormat(valueFormat(format))
                .code(code)
                .message(message)
                .tagValues(tagLengthValues)
                .build();
    }

    private static byte valueFormat(int format) {
        return (byte) (format & 0xFF);
    }
}
