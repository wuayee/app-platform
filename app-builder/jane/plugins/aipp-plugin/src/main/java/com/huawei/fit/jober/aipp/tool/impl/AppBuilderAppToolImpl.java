/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigFormDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import com.huawei.fit.jober.aipp.dto.AppCreateToolDto;
import com.huawei.fit.jober.aipp.enums.AppTypeEnum;
import com.huawei.fit.jober.aipp.service.AppBuilderAppService;
import com.huawei.fit.jober.aipp.tool.AppBuilderAppTool;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.core.formatters.OutputParser;
import modelengine.fel.core.formatters.json.JsonOutputParser;
import modelengine.fel.core.formatters.support.MarkdownParser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * app工具实现类
 *
 * @author 邬涨财
 * @since 2024-05-20
 */
@Component
public class AppBuilderAppToolImpl implements AppBuilderAppTool {
    private static final String DEFAULT_TENANT_ID = "31f20efc7e0848deab6a6bc10fc3021e";
    private static final String SYSTEM_PROMPT_KEY = "systemPrompt";
    private static final String DEFAULT_TEMPLATE_ID = "df87073b9bc85a48a9b01eccc9afccc4";
    private static final String INDEX_URL_FORMAT =
            "应用创建成功！ \n访问地址：{0}//#//app-develop//31f20efc7e0848deab6a6bc10fc3021e//app-detail//{1}";
    private static final Logger log = Logger.get(AppBuilderAppToolImpl.class);

    private final AppBuilderAppService appService;
    private final String appEngineUrl;
    private final ObjectSerializer objectSerializer;

    /**
     * 构造函数
     *
     * @param appService app服务
     * @param objectSerializer 序列化器
     * @param appEngineUrl app引擎url
     */
    public AppBuilderAppToolImpl(AppBuilderAppService appService, ObjectSerializer objectSerializer,
            @Value("${app-engine.endpoint}") String appEngineUrl) {
        this.appService = appService;
        this.appEngineUrl = appEngineUrl;
        this.objectSerializer = objectSerializer;
    }

    @Override
    @Fitable("default")
    public String createApp(String appInfo, String userId) {
        AppCreateToolDto dto;
        try {
            OutputParser<AppCreateToolDto> parser =
                    new MarkdownParser<>(JsonOutputParser.create(this.objectSerializer, AppCreateToolDto.class),
                            "json");
            dto = parser.parse(appInfo);
        } catch (SerializationException exception) {
            log.error("Failed to create app, parse json str error: {}", appInfo, exception);
            log.info("use default app attributes.");
            dto = AppCreateToolDto.builder()
                    .name("defaultApplicationCreatedAt" + System.currentTimeMillis())
                    .description("this is a default application.")
                    .icon("")
                    .greeting("hello world!")
                    .appType("")
                    .type(AppTypeEnum.APP.code())
                    .build();
        }
        dto.setAppType(StringUtils.isEmpty(dto.getAppType()) ? StringUtils.EMPTY : dto.getAppType());
        dto.setName(StringUtils.isEmpty(dto.getName()) ? StringUtils.EMPTY : dto.getName());
        dto.setGreeting(StringUtils.isEmpty(dto.getGreeting()) ? StringUtils.EMPTY : dto.getGreeting());
        dto.setDescription(StringUtils.isEmpty(dto.getDescription()) ? StringUtils.EMPTY : dto.getDescription());
        dto.setIcon(StringUtils.isEmpty(dto.getIcon()) ? StringUtils.EMPTY : dto.getIcon());
        dto.setType(StringUtils.isEmpty(dto.getType()) ? AppTypeEnum.APP.code() : dto.getType());
        OperationContext context = this.buildOperationContext(userId);
        AppBuilderAppDto appDto;
        try {
            appDto = this.appService.create(DEFAULT_TEMPLATE_ID, this.convert(dto), context, false);
        } catch (AippException exception) {
            log.error("Failed to create app: {}", exception.getMessage(), exception);
            return "创建应用失败：" + exception.getMessage();
        }
        if (StringUtils.isNotEmpty(dto.getSystemPrompt())) {
            this.updateConfig(dto, context, appDto, appDto.getConfig());
        }
        String appId = appDto.getId();
        return StringUtils.format(INDEX_URL_FORMAT, this.appEngineUrl, appId);
    }

    private void updateConfig(AppCreateToolDto dto, OperationContext context, AppBuilderAppDto appDto,
            AppBuilderConfigDto configDto) {
        if (configDto.getForm() == null) {
            return;
        }
        AppBuilderConfigFormDto form = configDto.getForm();
        List<AppBuilderConfigFormPropertyDto> properties = form.getProperties();
        if (CollectionUtils.isEmpty(properties)) {
            return;
        }
        Optional<AppBuilderConfigFormPropertyDto> systemPromptOptional = properties.stream()
                .filter(Objects::nonNull)
                .filter(property -> Objects.equals(property.getName(), SYSTEM_PROMPT_KEY))
                .findFirst();
        if (!systemPromptOptional.isPresent()) {
            return;
        }
        AppBuilderConfigFormPropertyDto systemPrompt = systemPromptOptional.get();
        systemPrompt.setDefaultValue(dto.getSystemPrompt());
        this.appService.updateConfig(appDto.getId(), configDto, context);
    }

    private AppBuilderAppCreateDto convert(AppCreateToolDto dto) {
        return AppBuilderAppCreateDto.builder()
                .appType(dto.getAppType())
                .description(dto.getDescription())
                .greeting(dto.getGreeting())
                .icon(dto.getIcon())
                .name(dto.getName())
                .type(dto.getType())
                .build();
    }

    private OperationContext buildOperationContext(String userId) {
        OperationContext context = new OperationContext();
        context.setTenantId(DEFAULT_TENANT_ID);
        context.setOperator(userId);
        return context;
    }
}
