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
import com.huawei.jade.common.Result;
import com.huawei.jade.store.entity.query.AppQuery;
import com.huawei.jade.store.entity.transfer.AppData;
import com.huawei.jade.store.service.AppService;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.HashSet;
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
    @WithSpan(value = "operation.app.upload")
    @PostMapping
    public Result<String> addApp(@RequestBody @SpanAttribute("name:$.name,version:$.version") AppData appData) {
        notNull(appData.getSchema(), "The app schema cannot be null.");
        Object name = appData.getSchema().get("name");
        notNull(name, "The app name cannot be null.");
        if ((name instanceof String) && StringUtils.isBlank((String) name)) {
            throw new IllegalArgumentException("The app name cannot be blank.");
        }
        return Result.ok(this.appService.publishApp(appData), 1);
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
     * @param mode 表示查询工具选择标签与和或的方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link AppData}{@code >}{@code >}。
     */
    @GetMapping("/search")
    public Result<List<AppData>> searchApps(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestQuery(value = "version", required = false) String version) {
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        AppQuery appQuery = new AppQuery.Builder().toolName(name)
                .includeTags(new HashSet<>(includeTags))
                .excludeTags(new HashSet<>(excludeTags))
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(version)
                .build();
        ListResult<AppData> res = this.appService.getApps(appQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 删除应用。
     *
     * @param uniqueName 表示应用的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @WithSpan(value = "operation.app.delete")
    @DeleteMapping("/{uniqueName}")
    public Result<String> deleteApp(@PathVariable("uniqueName") @SpanAttribute("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The unique name cannot be blank.");
        return Result.ok(this.appService.deleteApp(uniqueName), 1);
    }
}