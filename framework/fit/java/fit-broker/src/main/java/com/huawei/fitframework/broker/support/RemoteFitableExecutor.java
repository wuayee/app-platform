/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notEmpty;

import com.huawei.fit.client.Address;
import com.huawei.fit.client.Client;
import com.huawei.fit.client.Request;
import com.huawei.fit.client.RequestContext;
import com.huawei.fit.client.Response;
import com.huawei.fitframework.broker.Endpoint;
import com.huawei.fitframework.broker.ExceptionInfo;
import com.huawei.fitframework.broker.FitExceptionCreator;
import com.huawei.fitframework.broker.Fitable;
import com.huawei.fitframework.broker.FitableExecutor;
import com.huawei.fitframework.broker.Format;
import com.huawei.fitframework.broker.InvocationContext;
import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.RequestMetadata;
import com.huawei.fitframework.serialization.ResponseMetadata;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.serialization.Version;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 表示 {@link FitableExecutor} 的远程调用实现。
 *
 * @author 季聿阶
 * @since 2023-03-28
 */
public class RemoteFitableExecutor extends AbstractUnicastFitableExecutor {
    private static final Logger log = Logger.get(RemoteFitableExecutor.class);

    private final BeanContainer container;
    private final LazyLoader<FitExceptionCreator> exceptionCreatorLoader = new LazyLoader<>(this::getExceptionCreator);

    RemoteFitableExecutor(BeanContainer container) {
        this.container = container;
    }

    @Override
    protected Object execute(Fitable fitable, Target target, InvocationContext context, Object[] args) {
        this.validateTarget(fitable, target);
        log.debug("Prepare to invoke remote fitable. [id={}, target={}]", fitable.toUniqueId(), target);
        Format format = this.chooseFormat(target);
        RequestMetadata requestMetadataBytes = this.getRequestMetadataBytes(format, fitable);
        Method method = fitable.genericable().method().method();
        Response response = this.requestResponse(target, context, requestMetadataBytes, args, method);
        if (this.isSuccess(response.metadata())) {
            log.debug("Invoke remote fitable successfully. [id={}, target={}]", fitable.toUniqueId(), target);
            return response.data();
        }
        ExceptionInfo exceptionInfo = ExceptionInfo.fromFitableAndResponseMetadata(fitable, response.metadata());
        FitException responseException = this.exceptionCreatorLoader.get().buildException(exceptionInfo);
        log.error("Failed to invoke remote fitable. [id={}, target={}, message={}]",
                fitable.toUniqueId(),
                target,
                responseException.getMessage());
        log.debug("Exception: ", responseException);
        throw responseException;
    }

    private FitExceptionCreator getExceptionCreator() {
        return this.container.lookup(FitExceptionCreator.class)
                .map(BeanFactory::<FitExceptionCreator>get)
                .orElseThrow(() -> new IllegalStateException("No exception creator."));
    }

    private void validateTarget(Fitable fitable, Target target) {
        notBlank(target.workerId(), "The target worker id cannot be blank. [id={0}]", fitable.toUniqueId());
        notBlank(target.host(), "The target host cannot be blank. [id={0}]", fitable.toUniqueId());
        notBlank(target.environment(), "The target environment cannot be blank. [id={0}]", fitable.toUniqueId());
        notEmpty(target.endpoints(), "The target endpoints cannot be empty. [id={0}]", fitable.toUniqueId());
        notEmpty(target.formats(), "The target formats cannot be empty. [id={0}]", fitable.toUniqueId());
    }

    /**
     * 选择一个序列化协议。
     *
     * @param target 表示指定目标地址的 {@link Target}。
     * @return 表示选择的序列化协议的 {@link Format}。
     */
    protected Format chooseFormat(Target target) {
        return target.formats().iterator().next();
    }

    private RequestMetadata getRequestMetadataBytes(Format format, Fitable fitable) {
        return RequestMetadata.custom()
                .dataFormat(valueFormat(format.code()))
                .genericableId(fitable.genericable().id())
                .genericableVersion(Version.builder(fitable.genericable().version()).build())
                .fitableId(fitable.id())
                .fitableVersion(Version.builder(fitable.version()).build())
                .tagValues(this.getTlvFromSerializers())
                .build();
    }

    private static byte valueFormat(int format) {
        return (byte) (format & 0xFF);
    }

    private Client requireClient(String protocol) {
        return this.container.all(Client.class)
                .stream()
                .map(BeanFactory::<Client>get)
                .filter(client -> client.getSupportedProtocols().contains(protocol))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "No supported client at localhost. [protocol={0}]",
                        protocol)));
    }

    private TagLengthValues getTlvFromSerializers() {
        Map<Integer, byte[]> tlvMaps = Collections.emptyMap();
        TagLengthValues result = TagLengthValues.create();
        result.putTags(tlvMaps);
        return result;
    }

    private boolean isSuccess(ResponseMetadata responseMetadata) {
        return responseMetadata.code() == ResponseMetadata.CODE_OK;
    }

    private Response requestResponse(Target target, InvocationContext context, RequestMetadata metadata, Object[] args,
            Method method) {
        Endpoint endpoint = target.endpoints().iterator().next();
        Address address = Address.create(target.host(), endpoint.port());
        RequestContext requestContext = RequestContext.create(context.timeout(),
                context.timeoutUnit(),
                context.communicationType(),
                target.extensions());
        Type[] argumentTypes = this.getGenericParameterTypes(method, args);
        Request request = Request.custom()
                .protocol(endpoint.protocol())
                .address(address)
                .metadata(metadata)
                .dataTypes(argumentTypes)
                .data(args)
                .returnType(this.getGenericReturnType(method))
                .context(requestContext)
                .build();
        return this.requireClient(endpoint.protocol()).requestResponse(request);
    }

    /**
     * 获取泛化参数的类型数组。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @param args 表示调用参数的 {@link Object}{@code []}。
     * @return 表示泛化参数的类型数组的 {@link Type}{@code []}。
     */
    protected Type[] getGenericParameterTypes(Method method, Object[] args) {
        return Stream.of(method.getParameters()).map(Parameter::getParameterizedType).toArray(Type[]::new);
    }

    /**
     * 获取泛化返回值的类型。
     *
     * @param method 表示指定方法的 {@link Method}。
     * @return 表示泛化返回值的类型的 {@link Type}。
     */
    protected Type getGenericReturnType(Method method) {
        return method.getGenericReturnType();
    }
}
