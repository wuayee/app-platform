/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.controller;

import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNegative;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.carver.tool.model.query.ToolTagQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.repository.pgsql.model.reponse.Result;
import com.huawei.jade.carver.tool.service.ToolService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;

/**
 * 处理 HTTP 请求的控制器。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/10
 */
@Component
@RequestMapping("/tools")
public class ToolController {
    private static final String DECODE_EX = "[异常]: 解析中文异常";

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
        notBlank(tool.getName(), "the tool name cannot be blank");
        notNull(tool.getSchema(), "the tool schema cannot be null");
        return Result.createResult(this.toolService.addTool(tool), 0);
    }

    /**
     * 基于工具的唯一标识查询某个工具。
     *
     * @param uniqueName 表示工具的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link ToolData}{@code >}。
     */
    @GetMapping("/{uniqueName}")
    public Result<ToolData> getToolByUniqueName(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "the tool unique name cannot be blank");
        if (Objects.equals(this.decodeChinese(uniqueName), DECODE_EX)) {
            return Result.createResult(null, 0);
        }
        return Result.createResult(this.toolService.getTool(this.decodeChinese(uniqueName)), 0);
    }

    /**
     * 根据动态查询条件准确获取工具列表。
     *
     * @param toolName 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping
    public Result<List<ToolData>> getTools(@RequestParam(value = "name", required = false) String toolName,
            @RequestParam(value = "includeTags", required = false) List<String> includeTags,
            @RequestParam(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer limit) {
        String decodeToolName = StringUtils.EMPTY;
        if (toolName != null) {
            try {
                decodeToolName = URLDecoder.decode(toolName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return Result.createResult(null, 0);
            }
        }
        if (pageNum != null) {
            notNegative(pageNum, "the page num cannot be negative");
        }
        if (limit != null) {
            notNegative(limit, "the limit cannot be negative");
        }
        ToolTagQuery toolTagQuery = new ToolTagQuery(decodeToolName, includeTags, excludeTags, pageNum, limit);
        return Result.createResult(this.toolService.getTools(toolTagQuery), 0);
    }

    /**
     * 根据动态查询条件模糊获取工具列表。
     *
     * @param toolName 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping("/search")
    public Result<List<ToolData>> searchTools(@RequestParam(value = "toolName", required = false) String toolName,
            @RequestParam(value = "includeTags", required = false) List<String> includeTags,
            @RequestParam(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer limit) {
        String decodeToolName = StringUtils.EMPTY;
        if (toolName != null) {
            try {
                decodeToolName = URLDecoder.decode(toolName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return Result.createResult(null, 0);
            }
        }
        if (pageNum != null) {
            notNegative(pageNum, "the page num cannot be negative");
        }
        if (limit != null) {
            notNegative(limit, "the limit cannot be negative");
        }
        ToolTagQuery toolTagQuery = new ToolTagQuery(decodeToolName, includeTags, excludeTags, pageNum, limit);
        return Result.createResult(this.toolService.searchTools(toolTagQuery), 0);
    }

    /**
     * 删除工具。
     *
     * @param uniqueName 表示工具的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @DeleteMapping("/{uniqueName}")
    public Result<String> deleteTool(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "the unique name cannot be blank");
        if (Objects.equals(this.decodeChinese(uniqueName), DECODE_EX)) {
            return Result.createResult(DECODE_EX, 0);
        }
        return Result.createResult(this.toolService.deleteTool(this.decodeChinese(uniqueName)), 0);
    }

    private String decodeChinese(String input) {
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return DECODE_EX;
        }
    }
}
