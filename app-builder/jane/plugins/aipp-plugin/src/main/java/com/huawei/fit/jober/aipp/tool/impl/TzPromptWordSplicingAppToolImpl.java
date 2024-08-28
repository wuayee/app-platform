/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptDto;
import com.huawei.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import com.huawei.fit.jober.aipp.entity.AippInstLog;
import com.huawei.fit.jober.aipp.enums.AippInstLogType;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.service.AopAippLogService;
import com.huawei.fit.jober.aipp.service.AppBuilderPromptService;
import com.huawei.fit.jober.aipp.tool.TzPromptWordSplicingAppTool;
import com.huawei.fit.jober.aipp.util.JsonUtils;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 天舟AI提示词拼接工具实现类
 * 1、使用流程中的app_id找到对应的模板
 * 2、根据模板改写流程中的用户输入
 *
 * @author 晏钰坤
 * @since 2024/5/31
 */
@Component
public class TzPromptWordSplicingAppToolImpl implements TzPromptWordSplicingAppTool {
    private static final Logger log = Logger.get(TzPromptWordSplicingAppToolImpl.class);
    private static final String DEFAULT_TENANT_ID = "31f20efc7e0848deab6a6bc10fc3021e";
    private static final int TEMPLATE_TITLE_INDEX = 0;
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("<步骤[：:](.*?)>");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("<([^<>]*)>[:：]\\s*(.*?)(?=(<[^<>]*>[:：]\\s*|$))",
        Pattern.DOTALL);
    private static final String VARIABLE_TEMPLATE_FORMAT = "{{%s}}";
    private static final int VARIABLE_KEY_INDEX = 1;
    private static final int VARIABLE_VALUE_INDEX = 2;
    private static final String LINE_BREAK = "\n";
    private static final String EMPTY_STRING = "";

    private final AppBuilderPromptService appBuilderPromptService;
    private final AippLogService aippLogService;
    private final AopAippLogService aopAippLogService;

    public TzPromptWordSplicingAppToolImpl(AppBuilderPromptService appBuilderPromptService,
        AippLogService aippLogService, AopAippLogService aopAippLogService) {
        this.appBuilderPromptService = appBuilderPromptService;
        this.aippLogService = aippLogService;
        this.aopAippLogService = aopAippLogService;
    }

    @Override
    @Fitable("prompt.word.splice")
    public String promptWordSplice(String appId, String instanceId, String input) {
        // 判断输入中是否包含模板 不需要使用模板 直接返回String
        String title = input.split(LINE_BREAK)[TEMPLATE_TITLE_INDEX];
        String templateName = getTemplateName(title);
        if (StringUtils.isEmpty(templateName)) {
            return input;
        }

        log.info("[promptWordSplice]: Start a conversation with template name {}", templateName);
        try {
            Map<String, String> toBeReplacedVariables = getToBeReplacedVariables(input, title);
            // 根据appId查到灵感大全树
            String categoryId = getCategoryIdByTemplateName(appId, templateName);
            // 根据categoryId查询某个灵感模板的详情
            String promptTemplate = getPromptTemplate(appId, categoryId);
            if (StringUtils.isEmpty(promptTemplate)) {
                return input;
            }
            // 替换模板中的变量
            String overridePromptTemplate = overridePromptTemplate(toBeReplacedVariables, promptTemplate);
            log.info("[promptWordSplice]: overridePromptTemplate is {}", overridePromptTemplate);
            // 将改写后的写入历史记录
            writePromptLog(instanceId, overridePromptTemplate);
            return overridePromptTemplate;
        } catch (IllegalArgumentException e) {
            log.error("regex syntax error, error is {}", e.getMessage());
            return "天舟AI提示词拼接工具失败：" + e.getMessage();
        } catch (Exception e) {
            log.error("Failed to prompt word splice, error is {}", e.getMessage(), e);
            return "天舟AI提示词拼接工具失败：" + e.getMessage();
        }
    }

    private void writePromptLog(String instanceId, String promptTemplate) {
        List<AippInstLog> aippInstLogs = this.aippLogService.queryInstanceLogSince(instanceId, null);
        AippInstLog aippInstLog = aippInstLogs.get(0);
        AippLogCreateDto aippLogCreateDto = convertAippInstToDto(aippInstLog, promptTemplate);
        this.aopAippLogService.insertLog(aippLogCreateDto);
    }

    private String overridePromptTemplate(Map<String, String> toBeReplacedVariables, String promptTemplate) {
        String prompt = promptTemplate;
        for (String key : toBeReplacedVariables.keySet()) {
            if (prompt.contains(key)) {
                prompt = prompt.replace(key, toBeReplacedVariables.get(key));
            } else {
                log.info("Input key {} can not found in prompt template.", key);
            }
        }
        return prompt;
    }

    private String getPromptTemplate(String appId, String categoryId) {
        Rsp<AppBuilderPromptDto> inspirationsResponse = this.appBuilderPromptService.queryInspirations(appId,
            categoryId, this.buildOperationContext());
        if (Rsp.ok().getCode() != inspirationsResponse.getCode()) {
            log.info("Failed to query inspirations, appId is {}, category id is {}, error is {}",
                inspirationsResponse.getMsg());
        }
        return inspirationsResponse.getData().getInspirations().get(0).getPromptTemplate();
    }


    private String getCategoryIdByTemplateName(String appId, String templateName) {
        Rsp<List<AppBuilderPromptCategoryDto>> promptCategoriesResponse =
            this.appBuilderPromptService.listPromptCategories(appId, this.buildOperationContext());
        if (Rsp.ok().getCode() != promptCategoriesResponse.getCode()) {
            log.info("Failed to query list prompt categories, appId is {}, error message is {}", appId,
                promptCategoriesResponse.getMsg());
        }
        String categoryId = findIdByTemplateName(promptCategoriesResponse.getData(), templateName);
        if (StringUtils.isEmpty(categoryId)) {
            log.info("The category id corresponding to the template name {} is not found.", templateName);
        }
        return categoryId;
    }

    private Map<String, String> getToBeReplacedVariables(String input, String title) {
        String variableInput = input.replaceAll(title, EMPTY_STRING);
        Map<String, String> toBeReplacedVariables = new HashMap<>();
        Matcher matcherVar = VARIABLE_PATTERN.matcher(variableInput);
        while (matcherVar.find()) {
            String key = String.format(VARIABLE_TEMPLATE_FORMAT, matcherVar.group(VARIABLE_KEY_INDEX));
            String value = matcherVar.group(VARIABLE_VALUE_INDEX).trim();
            toBeReplacedVariables.put(key, value);
        }
        return toBeReplacedVariables;
    }

    private String getTemplateName(String title) {
        Matcher matcher = TEMPLATE_PATTERN.matcher(title);
        return matcher.find() ? matcher.group(1).trim() : EMPTY_STRING;
    }

    private AippLogCreateDto convertAippInstToDto(AippInstLog aippInstLog, String rewrittenInput) {
        Map<String, Object> map = JsonUtils.parseObject(aippInstLog.getLogData());
        map.put("msg", rewrittenInput);
        return AippLogCreateDto.builder()
            .aippId(aippInstLog.getAippId())
            .version(aippInstLog.getVersion())
            .aippType(aippInstLog.getAippType())
            .instanceId(aippInstLog.getInstanceId())
            .logType(AippInstLogType.HIDDEN_QUESTION.name())
            .logData(JsonUtils.toJsonString(map))
            .createUserAccount(aippInstLog.getCreateUserAccount())
            .path(aippInstLog.getPath())
            .build();
    }

    // 查找指定标题对应的categoryId
    private String findIdByTemplateName(List<AppBuilderPromptCategoryDto> data, String title) {
        for (AppBuilderPromptCategoryDto dto : data) {
            Optional<String> result = findIdByTitleRecursive(dto, title);
            if (result.isPresent()) {
                return result.get();
            }
        }
        return EMPTY_STRING;
    }

    // 递归查找标题对应的ID
    private Optional<String> findIdByTitleRecursive(AppBuilderPromptCategoryDto dto, String title) {
        if (dto.getTitle().equals(title)) {
            return Optional.of(dto.getId());
        }
        if (CollectionUtils.isEmpty(dto.getChildren())) {
            return Optional.empty();
        }
        for (AppBuilderPromptCategoryDto child : dto.getChildren()) {
            Optional<String> result = findIdByTitleRecursive(child, title);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    private OperationContext buildOperationContext() {
        OperationContext context = new OperationContext();
        context.setTenantId(DEFAULT_TENANT_ID);
        context.setOperator("com.huawei.jade");
        return context;
    }
}