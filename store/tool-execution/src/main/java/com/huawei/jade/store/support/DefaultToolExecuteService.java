/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.store.FunctionalTool;
import com.huawei.jade.store.ToolExecuteService;
import com.huawei.jade.store.ToolRepository;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link ToolExecuteService} 的默认实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-04-08
 */
@Component
public class DefaultToolExecuteService implements ToolExecuteService {
    private static final Logger log = Logger.get(DefaultToolExecuteService.class);

    private final ToolRepository repository;
    private final ObjectSerializer serializer;

    public DefaultToolExecuteService(ToolRepository repository, @Fit(alias = "json") ObjectSerializer serializer) {
        this.repository = repository;
        this.serializer = serializer;
    }

    @Override
    @Fitable(id = "standard")
    public String executeTool(String toolName, String jsonArgs) {
        try {
            return this.executeTool0(toolName, jsonArgs);
        } catch (Throwable cause) {
            log.warn("Failed to execute tool. [toolName={}, jsonArgs={}]", toolName, jsonArgs, cause);
            return this.makeErrorMessage(cause);
        }
    }

    private String executeTool0(String toolName, String jsonArgs) {
        notBlank(toolName, "The tool name cannot be blank.");
        Map<String, Object> mapArgs = this.serializer.deserialize(jsonArgs.getBytes(UTF_8),
                UTF_8,
                TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class}));
        FunctionalTool tool = cast(this.repository.getTool(toolName)
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("No tool. [name={0}]", toolName))));
        List<String> params = tool.parameterNames();
        List<Type> types = tool.parameters();
        Object[] args = new Object[params.size()];
        for (int i = 0; i < args.length; ++i) {
            args[i] = ObjectUtils.toCustomObject(mapArgs.get(params.get(i)), types.get(i));
        }
        Object result = tool.call(args);
        return new String(this.serializer.serialize(result, UTF_8), UTF_8);
    }

    private String makeErrorMessage(Throwable cause) {
        Map<Object, Object> error = MapBuilder.get().put("errorMessage", cause.getMessage()).build();
        return new String(this.serializer.serialize(error, UTF_8));
    }
}
