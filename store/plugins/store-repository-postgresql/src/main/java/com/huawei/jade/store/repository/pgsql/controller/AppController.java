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
import com.huawei.jade.store.entity.query.AppQuery;
import com.huawei.jade.store.entity.transfer.AppData;
import com.huawei.jade.store.service.AppService;

import java.util.List;

/**
 * 处理应用 HTTP 请求的控制器。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-20
 */
@Component
@RequestMapping("/store/apps")
public class AppController {
    private final AppService appService;

    /**
     * 通过应用服务来初始化 {@link AppController} 的新实例。
     *
     * @param appService 表示应用服务的 {@link AppService}。
     */
    public AppController(AppService appService) {
        this.appService = notNull(appService, "The app service cannot be null.");
    }

    /**
     * 添加应用。
     *
     * @param appData 表示 Http 请求的参数的 {@link AppData}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @PostMapping
    public Result<String> addApp(@RequestBody AppData appData) {
        notNull(appData.getSchema(), "The app schema cannot be null.");
        Object name = appData.getSchema().get("name");
        notNull(name, "The app name cannot be null.");
        if ((name instanceof String) && StringUtils.isBlank((String) name)) {
            throw new IllegalArgumentException("The app name cannot be blank.");
        }
        return Result.ok(this.appService.addApp(appData), 1);
    }

    /**
     * 基于应用的唯一标识查询某个应用。
     *
     * @param uniqueName 表示应用的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link AppData}{@code >}。
     */
    @GetMapping("/{uniqueName}")
    public Result<AppData> getAppByUniqueName(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The app unique name cannot be blank.");
        return Result.ok(this.appService.getApp(uniqueName), 1);
    }

    /**
     * 根据动态查询条件模糊获取应用列表。
     *
     * @param name 表示应用名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param pageNum 表示页码的 {@link Integer}。
     * @param limit 表示限制的 {@link Integer}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link AppData}{@code >}{@code >}。
     */
    @GetMapping("/search")
    public Result<List<AppData>> searchApps(
            @RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "orTags", defaultValue = "false", required = false) Boolean orTags,
            @RequestQuery(value = "pageNum", required = false) Integer pageNum,
            @RequestQuery(value = "pageSize", required = false) Integer limit) {
        if (pageNum != null) {
            notNegative(pageNum, "The page num cannot be negative.");
        }
        if (limit != null) {
            notNegative(limit, "The limit cannot be negative.");
        }
        AppQuery appQuery = new AppQuery(name, includeTags, excludeTags, orTags, pageNum, limit);
        ListResult<AppData> res = this.appService.getApps(appQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 删除应用。
     *
     * @param uniqueName 表示应用的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @DeleteMapping("/{uniqueName}")
    public Result<String> deleteApp(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The unique name cannot be blank.");
        return Result.ok(this.appService.deleteApp(uniqueName), 1);
    }
}