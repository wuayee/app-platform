/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.command;

import static modelengine.fitframework.inspection.Validation.notNull;

import lombok.Getter;
import lombok.Setter;
import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.jade.aipp.http.call.Authentication;
import modelengine.fit.jade.aipp.http.call.HttpBody;
import modelengine.fit.jade.aipp.http.call.enums.HttpBodyType;
import modelengine.fit.jade.aipp.http.call.enums.HttpRequestMethodType;
import modelengine.fit.jade.aipp.http.call.render.HttpCallTemplateRender;
import modelengine.fitframework.parameterization.ParameterizedStringResolver;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 表示http调用的命令。
 *
 * @author 张越
 * @since 2024-11-21
 */
public class HttpCallCommand {
    private static final ParameterizedStringResolver FORMATTER = ParameterizedStringResolver.create("{{", "}}", '\0',
            true);

    @Getter
    private HttpRequestMethodType method;

    @Setter
    private Map<String, Object> args;

    @Setter
    private String url;

    @Setter
    private HttpBody httpBody;

    @Getter
    @Setter
    private Integer timeout;

    @Setter
    private Map<String, String> headers;

    @Setter
    private Map<String, String> params;

    @Setter
    private Authentication authentication;

    private String completedUrl;

    private HttpCallTemplateRender render;

    public HttpCallCommand() {
        this.headers = new HashMap<>();
        this.params = new HashMap<>();
        this.args = new HashMap<>();
    }

    /**
     * 获取完整的url.
     * 1、解析pathArgs
     * 2、若有请求参数，则添加到url中.
     *
     * @return 完整url的 {@link String}.
     */
    public String getCompleteUrl() {
        if (this.completedUrl != null) {
            return this.completedUrl;
        }

        if (StringUtils.isBlank(this.url)) {
            return StringUtils.EMPTY;
        }

        String tmpUrl = this.render(this.url);
        Map<String, String> paramsMap = this.getParams();
        if (paramsMap == null || paramsMap.isEmpty()) {
            this.completedUrl = tmpUrl;
            return this.completedUrl;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tmpUrl);
        stringBuilder.append("?");
        StringJoiner joiner = new StringJoiner("&");
        paramsMap.forEach((key, value) -> joiner.add(key + "=" + value));
        stringBuilder.append(joiner);

        this.completedUrl = stringBuilder.toString();
        return this.completedUrl;
    }

    /**
     * 获取参数.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link String}{@code >}对象.
     */
    public Map<String, String> getParams() {
        return Optional.ofNullable(this.params)
                .map(m -> m.entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> this.render(e.getKey()), e -> this.render(e.getValue()))))
                .orElseGet(HashMap::new);
    }

    /**
     * 获取请求头.
     *
     * @return {@link Map}{@code <}{@link String}{@code ,}{@link String}{@code >}对象.
     */
    public Map<String, String> getHeaders() {
        return Optional.ofNullable(this.headers)
                .map(m -> m.entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> this.render(e.getKey()), e -> this.render(e.getValue()))))
                .orElseGet(HashMap::new);
    }

    /**
     * 设置http请求方法.
     *
     * @param method 方法类型字符串.
     */
    public void setMethod(String method) {
        this.method = HttpRequestMethodType.from(method);
    }

    /**
     * 获取 {@link Entity} 对象.
     *
     * @param message 请求消息对象.
     * @return {@link Optional}{@code <}{@link Entity}{@code >} 对象.
     */
    public Optional<Entity> getEntity(HttpMessage message) {
        return Optional.ofNullable(this.httpBody)
                .flatMap(b -> HttpBodyType.fromKey(b.getType())
                        .map(t -> t.getConverter().apply(new HttpBodyType.ConverterEntity(message, b, this))));
    }

    /**
     * 获取签名信息.
     *
     * @return {@link Optional}{@code <}{@link Authentication}{@code >} 对象.
     */
    public Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(this.authentication);
    }

    /**
     * 获取 httpClient 配置信息.
     *
     * @return {@link HttpClassicClientFactory.Config} 对象.
     */
    public HttpClassicClientFactory.Config getConfig() {
        if (this.timeout == null) {
            return HttpClassicClientFactory.Config.builder().build();
        }
        return HttpClassicClientFactory.Config.builder()
                .connectTimeout(this.timeout)
                .connectionRequestTimeout(this.timeout)
                .socketTimeout(this.timeout)
                .build();
    }

    /**
     * 校验command正确性.
     */
    public void validate() {
        notNull(this.method, "Http method cannot be null.");
        notNull(this.url, "Http url cannot be blank.");
    }

    /**
     * 渲染模板.
     *
     * @param template 模板字符串.
     * @return {@link String} 渲染后的字符串.
     */
    public String render(String template) {
        return this.getRender().render(template);
    }

    private HttpCallTemplateRender getRender() {
        if (this.render == null) {
            this.render = new HttpCallTemplateRender(this.stringifyArgs());
        }
        return this.render;
    }

    private Map<String, String> stringifyArgs() {
        return Optional.ofNullable(this.args)
                .map(m -> m.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    Object value = entry.getValue();
                    if (!(value instanceof String)) {
                        return com.alibaba.fastjson.JSON.toJSONString(value);
                    }
                    return ObjectUtils.cast(value);
                })))
                .orElseGet(HashMap::new);
    }
}
