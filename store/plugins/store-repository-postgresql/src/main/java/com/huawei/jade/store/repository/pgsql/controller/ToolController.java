/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.controller;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNegative;
import static com.huawei.fitframework.inspection.Validation.notNull;

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

import java.util.List;

/**
 * 处理 HTTP 请求的控制器。
 *
 * @author 李金绪 l00878072
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
    @PostMapping
    public Result<String> addTool(@RequestBody ToolData tool) {
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
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     * @param version 表示工具版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping
    public Result<List<ToolData>> getTools(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "pageNum", required = false) Integer pageNum,
            @RequestQuery(value = "pageSize", required = false) Integer limit,
            @RequestQuery(value = "version", required = false) String version) {
        if (pageNum != null) {
            notNegative(pageNum, "The page num cannot be negative. [pageNum={0}]", pageNum);
        }
        if (limit != null) {
            notNegative(limit, "The page size cannot be negative. [pageSize={0}]", limit);
        }
        ToolQuery toolQuery = new ToolQuery(name, includeTags, excludeTags, pageNum, limit, version);
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
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping("/{toolUniqueName}/versions")
    public Result<List<ToolData>> getAllToolVersions(
            @PathVariable(value = "toolUniqueName") String toolUniqueName,
            @RequestQuery(value = "pageNum", required = false) Integer pageNum,
            @RequestQuery(value = "pageSize", required = false) Integer limit) {
        notBlank(toolUniqueName, "The unique name cannot be blank.");
        ToolQuery toolQuery = new ToolQuery(
                toolUniqueName, null, null, pageNum, limit, null);
        ListResult<ToolData> res = this.toolService.getAllToolVersions(toolQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 根据动态查询条件模糊获取工具列表。
     *
     * @param name 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     * @param version 表示工具版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping("/search")
    public Result<List<ToolData>> searchTools(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "pageNum", required = false) Integer pageNum,
            @RequestQuery(value = "pageSize", required = false) Integer limit,
            @RequestQuery(value = "version", required = false) String version) {
        if (pageNum != null) {
            notNegative(pageNum, "The page num cannot be negative.");
        }
        if (limit != null) {
            notNegative(limit, "The limit cannot be negative.");
        }
        ToolQuery toolQuery = new ToolQuery(name, includeTags, excludeTags, pageNum, limit, version);
        ListResult<ToolData> res = this.toolService.searchTools(toolQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 删除工具的所有版本。
     *
     * @param uniqueName 表示工具的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @DeleteMapping("/{uniqueName}")
    public Result<String> deleteTool(@PathVariable("uniqueName") String uniqueName) {
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
    @DeleteMapping("/{uniqueName}/{version}")
    public Result<String> deleteToolByVersion(
            @PathVariable("uniqueName") String uniqueName,
            @PathVariable("version") String version) {
        notBlank(uniqueName, "The unique name cannot be blank.");
        return Result.ok(this.toolService.deleteToolByVersion(uniqueName, version));
    }
}