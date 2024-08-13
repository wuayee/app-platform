/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.controller;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNegative;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.jade.carver.validation.ValidateTagMode.validateTagMode;
import static com.huawei.jade.common.Result.calculateOffset;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.common.Result;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.HashSet;
import java.util.List;

/**
 * 处理 HTTP 请求的控制器。
 *
 * @author 李金绪
 * @since 2024-05-10
 */
@Component
@RequestMapping("/tools")
public class ToolController {
    private final ToolService toolService;

    /**
     * 通过工具服务和序列化工具来初始化 {@link ToolController} 的新实例。
     *
     * @param toolService 表示工具服务的 {@link ToolService}。
     */
    public ToolController(ToolService toolService) {
        this.toolService = notNull(toolService, "The tool service cannot be null.");
    }

    /**
     * 添加工具。
     *
     * @param tool 表示 Http 请求的参数的 {@link ToolData}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @WithSpan("operation.tool.upload")
    @PostMapping
    public Result<String> addTool(@RequestBody @SpanAttribute("name:$.name,version:$.version") ToolData tool) {
        notNull(tool.getSchema(), "The tool schema cannot be null.");
        Object name = tool.getSchema().get("name");
        notNull(name, "The tool name cannot be null.");
        if ((name instanceof String) && StringUtils.isBlank((String) name)) {
            throw new IllegalArgumentException("The tool name cannot be blank.");
        }
        return Result.ok(this.toolService.addTool(tool));
    }

    /**
     * 基于工具的唯一标识查询某个工具。
     *
     * @param uniqueName 表示工具的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link ToolData}{@code >}。
     */
    @GetMapping("/{uniqueName}")
    public Result<ToolData> getToolByUniqueName(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The tool unique name cannot be blank.");
        return Result.ok(this.toolService.getTool(uniqueName));
    }

    /**
     * 根据动态查询条件准确获取工具列表。
     *
     * @param name 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param mode 表示查询工具的标签选择与和或的方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param version 表示工具版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping
    public Result<List<ToolData>> getTools(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestQuery(value = "version", required = false) String version) {
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        ToolQuery toolQuery = new ToolQuery.Builder()
                .toolName(name)
                .includeTags(new HashSet<>(includeTags))
                .excludeTags(new HashSet<>(excludeTags))
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(version)
                .build();
        ListResult<ToolData> res = this.toolService.getTools(toolQuery);
        List<ToolData> data = res.getData();
        return Result.ok(data, res.getCount());
    }

    /**
     * 获取某个版本的工具。
     *
     * @param toolUniqueName 表示工具唯一标识的 {@link String}。
     * @param version 表示工具版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link ToolData}{@code >}。
     */
    @GetMapping("/{toolUniqueName}/{version}")
    public Result<ToolData> getToolVersion(
            @PathVariable(value = "toolUniqueName") String toolUniqueName,
            @PathVariable(value = "version") String version) {
        notBlank(toolUniqueName, "The unique name cannot be blank.");
        return Result.ok(this.toolService.getToolByVersion(toolUniqueName, version));
    }

    /**
     * 获取工具的所有版本。
     *
     * @param toolUniqueName 表示工具唯一标识的 {@link String}。
     * @param mode 表示工具标签查询的方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping("/{toolUniqueName}/versions")
    public Result<List<ToolData>> getAllToolVersions(
            @PathVariable(value = "toolUniqueName") String toolUniqueName,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize) {
        notBlank(toolUniqueName, "The unique name cannot be blank.");
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        ToolQuery toolQuery = new ToolQuery.Builder()
                .toolName(toolUniqueName)
                .includeTags(null)
                .excludeTags(null)
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(null)
                .build();
        ListResult<ToolData> res = this.toolService.getAllToolVersions(toolQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 根据动态查询条件模糊获取工具列表。
     *
     * @param name 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param mode 表示查询工具的标签与和或的方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param version 表示工具版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping("/search")
    public Result<List<ToolData>> searchTools(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestQuery(value = "version", required = false) String version) {
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        ToolQuery toolQuery = new ToolQuery.Builder()
                .toolName(name)
                .includeTags(new HashSet<>(includeTags))
                .excludeTags(new HashSet<>(excludeTags))
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(version)
                .build();
        ListResult<ToolData> res = this.toolService.searchTools(toolQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 删除工具的所有版本。
     *
     * @param uniqueName 表示工具的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @WithSpan("operation.tool.delete")
    @DeleteMapping("/{uniqueName}")
    public Result<String> deleteTool(@PathVariable("uniqueName") @SpanAttribute("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The unique name cannot be blank.");
        return Result.ok(this.toolService.deleteTool(uniqueName));
    }

    /**
     * 删除工具的某个版本。
     *
     * @param uniqueName 表示工具的唯一索引的 {@link String}。
     * @param version 表示工具版本的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @WithSpan("operation.tool.deleteVersion")
    @DeleteMapping("/{uniqueName}/{version}")
    public Result<String> deleteToolByVersion(
            @PathVariable("uniqueName") @SpanAttribute("uniqueName") String uniqueName,
            @PathVariable("version") @SpanAttribute("version") String version) {
        notBlank(uniqueName, "The unique name cannot be blank.");
        return Result.ok(this.toolService.deleteToolByVersion(uniqueName, version));
    }
}