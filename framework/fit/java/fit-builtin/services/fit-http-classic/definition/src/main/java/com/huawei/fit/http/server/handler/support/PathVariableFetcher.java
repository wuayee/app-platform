/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static com.huawei.fit.http.HttpClassicRequestAttribute.PATH_PATTERN;
import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.HttpClassicServerResponse;
import com.huawei.fit.http.server.handler.RequestMappingException;
import com.huawei.fit.http.server.handler.SourceFetcher;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 表示从 Http 请求路径中获取值的 {@link SourceFetcher}。
 *
 * @author 季聿阶
 * @since 2022-08-28
 */
public class PathVariableFetcher implements SourceFetcher {
    private static final char PATH_SEPARATOR = '/';
    private static final char PATH_VARIABLE_PREFIX = '{';
    private static final char PATH_VARIABLE_SUFFIX = '}';

    private final String variableName;

    /**
     * 通过路径变量名来实例化 {@link PathVariableFetcher}。
     *
     * @param variableName 表示路径变量名的 {@link String}。
     * @throws IllegalArgumentException 当 {@code variableName} 为 {@code null} 或空白字符串时。
     */
    public PathVariableFetcher(String variableName) {
        this.variableName = notBlank(variableName, "The path variable cannot be blank.");
    }

    @Override
    public Object get(HttpClassicServerRequest request, HttpClassicServerResponse response) {
        String pathPattern = cast(request.attributes()
                .get(PATH_PATTERN.key())
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("No path pattern. [path={0}]",
                        request.path()))));
        List<String> partPatterns =
                StringUtils.split(pathPattern, PATH_SEPARATOR, ArrayList::new, StringUtils::isNotBlank);
        int pathVariableIndex = this.findPathVariableIndex(partPatterns);
        Validation.between(pathVariableIndex,
                0,
                partPatterns.size() - 1,
                () -> new RequestMappingException(StringUtils.format(
                        "No path variable in path pattern. [pattern={0}, variable={1}]",
                        pathPattern,
                        this.variableName)));
        List<String> partPath =
                StringUtils.split(request.path(), PATH_SEPARATOR, ArrayList::new, StringUtils::isNotBlank);
        Validation.equals(partPath.size(),
                partPatterns.size(),
                () -> new RequestMappingException(StringUtils.format(
                        "The http request path does not match the path pattern. [pattern={0}, path={1}]",
                        pathPattern,
                        request.path())));
        return partPath.get(pathVariableIndex);
    }

    private int findPathVariableIndex(List<String> partPatterns) {
        for (int i = 0; i < partPatterns.size(); i++) {
            if (Objects.equals(partPatterns.get(i),
                    StringUtils.surround(this.variableName, PATH_VARIABLE_PREFIX, PATH_VARIABLE_SUFFIX))) {
                return i;
            }
        }
        return -1;
    }
}
