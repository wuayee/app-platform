/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support;

import static com.huawei.fitframework.inspection.Validation.isInstanceOf;
import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.client.HttpClientException;
import com.huawei.fit.http.client.proxy.DestinationSetter;
import com.huawei.fit.http.client.proxy.HttpEmitter;
import com.huawei.fit.http.client.proxy.PropertyValueApplier;
import com.huawei.fit.http.client.proxy.emitter.DefaultHttpEmitter;
import com.huawei.fit.http.client.proxy.support.applier.MultiDestinationsPropertyValueApplier;
import com.huawei.fit.http.client.proxy.support.setter.CookieDestinationSetter;
import com.huawei.fit.http.client.proxy.support.setter.DestinationSetterInfo;
import com.huawei.fit.http.client.proxy.support.setter.FormUrlEncodedEntitySetter;
import com.huawei.fit.http.client.proxy.support.setter.HeaderDestinationSetter;
import com.huawei.fit.http.client.proxy.support.setter.ObjectEntitySetter;
import com.huawei.fit.http.client.proxy.support.setter.PathVariableDestinationSetter;
import com.huawei.fit.http.client.proxy.support.setter.QueryDestinationSetter;
import com.huawei.fit.http.entity.ObjectEntity;
import com.huawei.fit.http.entity.TextEntity;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.http.protocol.HttpResponseStatus;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.value.ValueFetcher;
import com.huawei.jade.carver.tool.Tool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 表示 {@link Tool} 的 Http 调用实现。
 *
 * @author 季聿阶
 * @since 2024-05-10
 */
public class HttpTool extends AbstractTool {
    /**
     * 表示 HTTP 调用工具的类型。
     */
    public static final String TYPE = "HTTP";
    private static final String KEY_OF_METHOD = "method";
    private static final String KEY_OF_PROTOCOL = "protocol";
    private static final String KEY_OF_DOMAIN = "domain";
    private static final String KEY_OF_PATH_PATTERN = "pathPattern";
    private static final String KEY_OF_MAPPINGS = "mappings";
    private static final String KEY_OF_HTTP_SOURCE = "httpSource";
    private static final String KEY_OF_PARAMETERS = "parameters";
    private static final String KEY_OF_ORDER = "order";
    private static final String KEY_OF_KEY_VALUE_SETTER = "key";
    private static final String KEY_OF_ENTITY_SETTER = "propertyPath";

    private final HttpClassicClientFactory factory;
    private final ValueFetcher valueFetcher;
    private final HttpEmitter emitter;
    private final Map<String, Function<Map<String, String>, DestinationSetter>> destinationSetterCreators =
            MapBuilder.<String, Function<Map<String, String>, DestinationSetter>>get()
                    .put("COOKIE", this::createCookieDestinationSetter)
                    .put("FORM_URL_ENCODED_ENTITY", this::createFormUrlEncodedEntitySetter)
                    .put("HEADER", this::createHeaderDestinationSetter)
                    .put("OBJECT_ENTITY", this::createObjectEntitySetter)
                    .put("PATH_VARIABLE", this::createPathVariableDestinationSetter)
                    .put("QUERY", this::createQueryDestinationSetter)
                    .build();

    /**
     * 通过工具的基本信息和工具元数据来初始化 {@link AbstractTool} 的新实例。
     *
     * @param factory 表示 Http 客户端的工厂的 {@link HttpClassicClientFactory}。
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param valueFetcher 表示值获取工具的 {@link ValueFetcher}。
     * @param itemInfo 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工具的元数据的 {@link Metadata}。
     */
    protected HttpTool(HttpClassicClientFactory factory, ObjectSerializer serializer, ValueFetcher valueFetcher,
            Info itemInfo, Metadata metadata) {
        super(serializer, itemInfo, metadata);
        this.factory = notNull(factory, "The http classic client factory cannot be null.");
        this.valueFetcher = notNull(valueFetcher, "The value fetcher cannot be null.");
        this.emitter = this.createHttpEmitter();
    }

    @Override
    public Object execute(Object... args) throws HttpClientException {
        HttpClassicClientResponse<Object> response = this.emitter.emit(args);
        if (response.statusCode() == HttpResponseStatus.NO_CONTENT.statusCode()) {
            return null;
        }
        isTrue(response.entity().isPresent(), () -> new HttpClientException("Cannot get response entity."));
        if (response.entity().get() instanceof ObjectEntity) {
            return response.objectEntity().get().object();
        } else if (response.entity().get() instanceof TextEntity) {
            return response.textEntity().get().content();
        } else {
            return null;
        }
    }

    private HttpEmitter createHttpEmitter() {
        Map<String, Object> schema = this.info().schema();
        Map<String, Object> httpRunnables = getElementFromMap(this.info().runnables(), TYPE, Map.class);
        String methodAsString = getElementFromMap(httpRunnables, KEY_OF_METHOD, String.class);
        HttpRequestMethod method = notNull(HttpRequestMethod.from(methodAsString),
                StringUtils.format("Cannot create HttpRequestMethod by field \"{0}\". [methodAsString={1}]",
                        KEY_OF_METHOD,
                        methodAsString));
        String protocol = getElementFromMap(httpRunnables, KEY_OF_PROTOCOL, String.class);
        String domain = getElementFromMap(httpRunnables, KEY_OF_DOMAIN, String.class);
        String pathPattern = getElementFromMap(httpRunnables, KEY_OF_PATH_PATTERN, String.class);
        Map<String, Map<String, Map<String, String>>> mappings =
                getElementFromMap(httpRunnables, KEY_OF_MAPPINGS, Map.class);
        Map<String, Object> parameters = getElementFromMap(schema, KEY_OF_PARAMETERS, Map.class);
        List<String> order = getElementFromMap(parameters, KEY_OF_ORDER, List.class);
        if (order.size() != mappings.size() || !isSameSet(new HashSet<>(order), mappings.keySet())) {
            throw new IllegalArgumentException("Arguments in order is not same to key in mappings.");
        }
        List<PropertyValueApplier> appliers = this.createAppliers(order, mappings);
        return new DefaultHttpEmitter(appliers, this.factory.create(), method, protocol, domain, pathPattern);
    }

    private List<PropertyValueApplier> createAppliers(List<String> order,
            Map<String, Map<String, Map<String, String>>> mappings) {
        List<PropertyValueApplier> appliers = new ArrayList<>();
        for (String argumentName : order) {
            List<DestinationSetterInfo> setterInfos = new ArrayList<>();
            for (Map.Entry<String, Map<String, String>> entry : mappings.get(argumentName).entrySet()) {
                String jsonPath = entry.getKey();
                Map<String, String> info = entry.getValue();
                DestinationSetter destinationSetter = this.createDestinationSetter(info);
                DestinationSetterInfo destinationSetterInfo = new DestinationSetterInfo(destinationSetter, jsonPath);
                setterInfos.add(destinationSetterInfo);
            }
            appliers.add(new MultiDestinationsPropertyValueApplier(setterInfos, this.valueFetcher));
        }
        return appliers;
    }

    private DestinationSetter createDestinationSetter(Map<String, String> info) {
        isTrue(info.containsKey(KEY_OF_HTTP_SOURCE), "Setter info must contain field httpSource.");
        String httpSource = info.get(KEY_OF_HTTP_SOURCE);
        isTrue(this.destinationSetterCreators.containsKey(httpSource),
                StringUtils.format("Cannot find destination setter creator. [httpSource={0}]", httpSource));
        return this.destinationSetterCreators.get(httpSource).apply(info);
    }

    private DestinationSetter createCookieDestinationSetter(Map<String, String> info) {
        isTrue(info.containsKey(KEY_OF_KEY_VALUE_SETTER),
                "Setter info of cookie destination setter must contain field key.");
        return new CookieDestinationSetter(info.get(KEY_OF_KEY_VALUE_SETTER));
    }

    private DestinationSetter createFormUrlEncodedEntitySetter(Map<String, String> info) {
        isTrue(info.containsKey(KEY_OF_KEY_VALUE_SETTER),
                "Setter info of form url encode destination setter must contain field key.");
        return new FormUrlEncodedEntitySetter(info.get(KEY_OF_KEY_VALUE_SETTER));
    }

    private DestinationSetter createHeaderDestinationSetter(Map<String, String> info) {
        isTrue(info.containsKey(KEY_OF_KEY_VALUE_SETTER),
                "Setter info of header destination setter must contain field key.");
        return new HeaderDestinationSetter(info.get(KEY_OF_KEY_VALUE_SETTER));
    }

    private DestinationSetter createObjectEntitySetter(Map<String, String> info) {
        isTrue(info.containsKey(KEY_OF_ENTITY_SETTER),
                "Setter info of object entity destination setter must contain field propertyPath.");
        return new ObjectEntitySetter(info.get(KEY_OF_ENTITY_SETTER));
    }

    private DestinationSetter createPathVariableDestinationSetter(Map<String, String> info) {
        isTrue(info.containsKey(KEY_OF_KEY_VALUE_SETTER),
                "Setter info of path variable destination setter must contain field key.");
        return new PathVariableDestinationSetter(info.get(KEY_OF_KEY_VALUE_SETTER));
    }

    private DestinationSetter createQueryDestinationSetter(Map<String, String> info) {
        isTrue(info.containsKey(KEY_OF_KEY_VALUE_SETTER),
                "Setter info of query destination setter must contain field key.");
        return new QueryDestinationSetter(info.get(KEY_OF_KEY_VALUE_SETTER));
    }

    private static <T> T getElementFromMap(Map<String, Object> map, String fieldName, Class<T> clazz) {
        isTrue(map.containsKey(fieldName), StringUtils.format("The map not contains field \"{0}\"", fieldName));
        isInstanceOf(map.get(fieldName),
                clazz,
                "The value of the field \"{0}\" is not an instance of {1}.",
                fieldName,
                clazz);
        return cast(map.get(fieldName));
    }

    private static <T> boolean isSameSet(Set<T> first, Set<T> second) {
        if (first.size() != second.size()) {
            return false;
        }
        return first.containsAll(second);
    }
}