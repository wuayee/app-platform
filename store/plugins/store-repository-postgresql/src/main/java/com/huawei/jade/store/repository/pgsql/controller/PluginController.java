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
import com.huawei.jade.common.Result;
import com.huawei.jade.store.entity.query.PluginQuery;
import com.huawei.jade.store.entity.transfer.PluginData;
import com.huawei.jade.store.service.PluginService;

import java.util.List;

/**
 * 处理插件 HTTP 请求的控制器。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-20
 */
@Component
@RequestMapping("/store/plugins")
public class PluginController {
    private final PluginService pluginService;

    /**
     * 通过插件服务来初始化 {@link PluginController} 的新实例。
     *
     * @param pluginService 表示插件服务的 {@link PluginService}。
     */
    public PluginController(PluginService pluginService) {
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
    }

    /**
     * 添加工具。
     *
     * @param pluginData 表示 Http 请求的参数的 {@link PluginData}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @PostMapping
    public Result<String> addPlugin(@RequestBody PluginData pluginData) {
        notNull(pluginData.getSchema(), "The tool schema cannot be null.");
        Object name = pluginData.getSchema().get("name");
        notNull(name, "The tool name cannot be null.");
        if ((name instanceof String) && StringUtils.isBlank((String) name)) {
            throw new IllegalArgumentException("The tool name cannot be blank.");
        }
        return Result.ok(this.pluginService.addPlugin(pluginData), 1);
    }

    /**
     * 基于插件的唯一标识查询某个插件。
     *
     * @param uniqueName 表示插件的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link PluginData}{@code >}。
     */
    @GetMapping("/{uniqueName}")
    public Result<PluginData> getPluginByUniqueName(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The plugin unique name cannot be blank.");
        return Result.ok(this.pluginService.getPlugin(uniqueName), 1);
    }

    /**
     * 根据动态查询条件模糊获取插件列表。
     *
     * @param isPublished 表示插件是否发布的 {@link Boolean}。
     * @param owner 表示插件的拥有者的 {@link String}。
     * @param collector 表示收藏者的名字的 {@link String}。
     * @param name 表示插件名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param orTags 表示查询工具的标签与和或方式的 {@link Boolean}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link PluginData}{@code >}{@code >}。
     */
    @GetMapping("/search")
    public Result<List<PluginData>> searchPlugins(
            @RequestQuery(value = "isPublished", required = false) Boolean isPublished,
            @RequestQuery(value = "owner", required = false) String owner,
            @RequestQuery(value = "collector", required = false) String collector,
            @RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "orTags", defaultValue = "false", required = false) Boolean orTags,
            @RequestQuery(value = "pageNum", required = false) Integer pageNum,
            @RequestQuery(value = "pageSize", required = false) Integer limit,
            @RequestQuery(value = "version", required = false) String version) {
        if (pageNum != null) {
            notNegative(pageNum, "The page num cannot be negative.");
        }
        if (limit != null) {
            notNegative(limit, "The limit cannot be negative.");
        }
        PluginQuery pluginQuery = new PluginQuery(isPublished, owner, collector,
                name, includeTags, excludeTags,orTags, pageNum, limit, version);
        ListResult<PluginData> res = this.pluginService.getPlugins(pluginQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 删除插件。
     *
     * @param uniqueName 表示插件的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @DeleteMapping("/{uniqueName}")
    public Result<String> deletePlugin(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The unique name cannot be blank.");
        return Result.ok(this.pluginService.deletePlugin(uniqueName), 1);
    }
}