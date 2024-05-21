/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.enums.AppTypeEnum;
import com.huawei.fit.jober.aipp.service.AppBuilderAppService;
import com.huawei.fit.jober.aipp.tool.AppBuilderAppTool;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-20
 */
@Component
public class AppBuilderAppToolImpl implements AppBuilderAppTool {
    private static final String DEFAULT_TENANT_ID = "31f20efc7e0848deab6a6bc10fc3021e";
    private final AppBuilderAppService appService;
    private static final String DEFAULT_TEMPLATE_ID = "df87073b9bc85a48a9b01eccc9afccc4";
    private static final String INDEX_URL_FORMAT =
            "应用创建成功，链接为：{0}//appbuilder//#//aipp//31f20efc7e0848deab6a6bc10fc3021e//detail//{1}";
    private final String janeUrl;
    private static final Logger log = Logger.get(AppBuilderAppToolImpl.class);

    public AppBuilderAppToolImpl(AppBuilderAppService appService, @Value("${jane.endpoint}") String janeUrl) {
        this.appService = appService;
        this.janeUrl = janeUrl;
    }

    @Override
    @Fitable("default")
    public String createApp(String appInfo) {
        AppBuilderAppCreateDto dto;
        try {
            dto = JsonUtils.parseObject(appInfo, AppBuilderAppCreateDto.class);
        } catch (Exception exception) {
            log.error("Failed to create app, parse json str error: {}", appInfo, exception);
            return "创建应用失败，请重试";
        }
        dto.setAppType(StringUtils.isEmpty(dto.getAppType()) ? StringUtils.EMPTY : dto.getAppType());
        dto.setName(StringUtils.isEmpty(dto.getName()) ? StringUtils.EMPTY : dto.getName());
        dto.setGreeting(StringUtils.isEmpty(dto.getGreeting()) ? StringUtils.EMPTY : dto.getGreeting());
        dto.setDescription(StringUtils.isEmpty(dto.getDescription()) ? StringUtils.EMPTY : dto.getDescription());
        dto.setIcon(StringUtils.isEmpty(dto.getIcon()) ? StringUtils.EMPTY : dto.getIcon());
        dto.setType(StringUtils.isEmpty(dto.getType()) ? AppTypeEnum.APP.code() : dto.getType());
        AppBuilderAppDto appDto;
        try {
            appDto = this.appService.create(DEFAULT_TEMPLATE_ID, dto, this.buildOperationContext());
        } catch (Exception exception) {
            log.error("Failed to create app: {}", exception.getMessage(), exception);
            return "创建应用失败：" + exception.getMessage();
        }
        String appId = appDto.getId();
        return StringUtils.format(INDEX_URL_FORMAT, this.janeUrl, appId);
    }

    private OperationContext buildOperationContext() {
        OperationContext context = new OperationContext();
        context.setTenantId(DEFAULT_TENANT_ID);
        context.setOperator("com.huawei.jade");
        return context;
    }
}
