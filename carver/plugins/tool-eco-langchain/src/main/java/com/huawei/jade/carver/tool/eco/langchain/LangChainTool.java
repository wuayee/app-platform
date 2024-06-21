/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.eco.langchain;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.tool.support.AbstractTool;
import com.huawei.jade.fel.service.langchain.LangChainRunnableService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 表示 {@link com.huawei.jade.carver.tool.Tool} 的
 * <a href="https://python.langchain.com/v0.2/docs/integrations/tools/">LangChain</a> 的实现。
 *
 * @author 刘信宏
 * @since 2024-06-19
 */
public class LangChainTool extends AbstractTool {
    private static final String TOOL_TYPE = "LangChain";
    private static final String GENERICBLE = "genericableId";
    private static final String FITABLE = "fitableId";

    private final LangChainRunnableService runnableService;

    /**
     * 通过 Json 序列化器、工具的基本信息和工具元数据来初始化 {@link LangChainTool} 的新实例。
     *
     * @param serializer 表示 Json 序列化器的 {@link ObjectSerializer}。
     * @param runnableService 表示 LangChain 提供的 runnable 服务的 {@link LangChainRunnableService}。
     * @param itemInfo 表示工具的基本信息的 {@link Info}。
     * @param metadata 表示工具的元数据的 {@link Metadata}。
     */
    protected LangChainTool(LangChainRunnableService runnableService, ObjectSerializer serializer, Info itemInfo,
            Metadata metadata) {
        super(serializer, itemInfo, metadata);
        this.runnableService = Validation.notNull(runnableService, "The LangChain runnable service cannot be null.");
    }

    @Override
    public Object execute(Object... args) {
        List<Object> argsValue = Arrays.asList(args);
        List<String> argsName = this.metadata().parameterNames();
        Validation.equals(argsValue.size(), argsName.size(), "The size of names and values must be equal.");

        String toolType = StringUtils.toUpperCase(TOOL_TYPE);
        Map<String, Object> runnable = cast(this.info().runnables().get(toolType));
        if (MapUtils.isEmpty(runnable)) {
            throw new IllegalStateException(StringUtils.format("No runnable info. [type={0}]", toolType));
        }
        String genericableId = cast(runnable.get(GENERICBLE));
        Validation.notBlank(genericableId, "No genericable id in runnable info.");
        String fitableId = cast(runnable.get(FITABLE));
        Validation.notBlank(fitableId, "No fitable id in runnable info.");

        Map<String, Object> paramsMap = IntStream.range(0, argsName.size())
                .boxed()
                .collect(Collectors.toMap(argsName::get, argsValue::get));
        return this.runnableService.invoke(genericableId, fitableId, paramsMap);
    }
}
