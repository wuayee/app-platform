/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.controller;

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
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.jade.store.model.query.ToolTagQuery;
import com.huawei.jade.store.model.reponse.Result;
import com.huawei.jade.store.model.transfer.ToolData;
import com.huawei.jade.store.service.ToolService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 旧版适配。
 *
 * @author 李金绪 l00878072
 * @since 2024/5/14
 */
@Component
@RequestMapping("/store/platform")
public class AdapterController {
    private static final Logger log = Logger.get(ToolController.class);
    private static final String DECODE_EX = "[异常]: 解析中文异常";

    private final ToolService toolService;
    private final ObjectSerializer serializer;

    /**
     * 适配 ToolController 的 {@link AdapterController} 的新实例。
     *
     * @param toolService 表示商品通用服务的 {@link ToolService}。
     * @param serializer 表示序列化的 {@link ObjectSerializer}。
     */
    public AdapterController(ToolService toolService, @Fit(alias = "json") ObjectSerializer serializer) {
        this.toolService = notNull(toolService, "The tool service cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    /**
     * 添加 FitTool 的 Http Post 请求。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param toolName 表示 FitTool 名字的 {@link String}。
     * @param genericableId 表示分组的 {@link String}。
     * @param fitableId 表示泛服务实现的 {@link String}。
     * @param schema 表示 FitTool 格式的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @PostMapping(path = "/{platform}/fit/tool/names/{toolName}")
    public Result<String> addFitTool(@PathVariable("platform") String platform,
            @PathVariable("toolName") String toolName, @RequestParam("genericableId") String genericableId,
            @RequestParam("fitableId") String fitableId, @RequestBody Map<String, Object> schema) {
        if (this.decodeChinese(toolName).equals(DECODE_EX)) {
            return Result.createResult(DECODE_EX, 0);
        }
        Map<String, Object> runnables = new HashMap<>();
        runnables.put("FIT", this.setFitRunnable(genericableId, fitableId));
        HashSet<String> tags = new HashSet<>();
        tags.add("FIT");
        if (schema.containsKey("tags") && schema.get("tags") instanceof List<?>) {
            tags.addAll((List<String>) schema.get("tags"));
        }
        ToolData tool = this.convertToolData(this.decodeChinese(toolName), schema, runnables, tags);
        return Result.createResult(this.toolService.addTool(tool), 0);
    }

    /**
     * 添加商品的 Http Post 请求。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param category 表示商品分类的 {@link String}。
     * @param genericableId 表示分组的 {@link String}。
     * @param itemName 表示商品名字的 {@link String}。
     * @param tags 表示商品标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param schema 表示 FitTool 格式的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @PostMapping(path = "/{platform}/categories/{category}/groups/{genericableId}/names/{itemName}")
    public Result<String> addItem(@PathVariable("platform") String platform, @PathVariable("category") String category,
            @PathVariable("genericableId") String genericableId, @PathVariable("itemName") String itemName,
            @RequestParam("tags") List<String> tags, @RequestBody Map<String, Object> schema) {
        if (this.decodeChinese(genericableId).equals(DECODE_EX) || this.decodeChinese(itemName).equals(DECODE_EX)) {
            return Result.createResult(DECODE_EX, 0);
        }
        ToolData tool = this.convertToolData(this.decodeChinese(itemName), schema, null, new HashSet<>(tags));
        return Result.createResult(this.toolService.addTool(tool), 0);
    }

    /**
     * 根据分类和标签查询所有商品的 Http Get 请求。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param category 表示商品分类的 {@link String}。
     * @param includeTags 表示需要包括的标签的 {@link String}。
     * @param excludeTags 表示不需要包括的标签的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param limit 表示分页查询数量限制的 {@code int}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping(path = "/{platform}/categories/{category}")
    public Result<List<ToolData>> getAllItems(@PathVariable("platform") String platform,
            @PathVariable("category") String category, @RequestParam("includeTags") List<String> includeTags,
            @RequestParam("excludeTags") List<String> excludeTags, @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int limit) {
        notNegative(pageNum, "the page num cannot be negative. [pageNum={0}]", pageNum);
        notNegative(limit, "the page size cannot be negative. [pageSize={0}]", limit);
        ToolTagQuery toolTagQuery = new ToolTagQuery(null, includeTags, excludeTags, pageNum, limit);
        return Result.createResult(this.toolService.getTools(toolTagQuery), 0);
    }

    /**
     * 查询所有的 genericables。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param limit 表示分页查询数量限制的 {@code int}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link Set}{@code <}{@link String}{@code >}{@code >}。
     */
    @GetMapping(path = "/{platform}/fit/tool/genericables")
    public Result<Set<String>> getAllGenericableIds(@PathVariable("platform") String platform,
            @RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int limit) {
        notNegative(pageNum, "the page num cannot be negative. [pageNum={0}]", pageNum);
        notNegative(limit, "the page size cannot be negative. [pageSize={0}]", limit);
        ToolTagQuery toolTagQuery =
                new ToolTagQuery(null, new HashSet<>(Collections.singleton("FIT")), null, null, pageNum, limit);
        List<ToolData> tools = this.toolService.getTools(toolTagQuery);
        Set<String> genericableIds = tools.stream()
                .filter(toolData -> toolData.getRunnables() != null && toolData.getRunnables().containsKey("FIT"))
                .map(toolData -> (Map<String, Object>) toolData.getRunnables().get("FIT"))
                .filter(fitMap -> fitMap.get("genericableId") instanceof String)
                .map(fitMap -> (String) fitMap.get("genericableId"))
                .collect(Collectors.toSet());
        return Result.createResult(genericableIds, 0);
    }

    /**
     * 根据商品分类、分组和标签查询所有商品。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param category 表示商品分类的 {@link String}。
     * @param genericableId 表示商品的泛服务标识的 {@link String}。
     * @param includeTags 表示要包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示不包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param limit 表示分页查询的数量限制的 {@code int}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping(path = "/{platform}/categories/{category}/groups/{genericableId}")
    public Result<List<ToolData>> getItemsByGroup(@PathVariable("platform") String platform,
            @PathVariable("category") String category, @PathVariable("genericableId") String genericableId,
            @RequestParam("includeTags") List<String> includeTags,
            @RequestParam("excludeTags") List<String> excludeTags, @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int limit) {
        notNegative(pageNum, "the page num cannot be negative. [pageNum={0}]", pageNum);
        notNegative(limit, "the page size cannot be negative. [pageSize={0}]", limit);
        if (this.decodeChinese(genericableId).equals(DECODE_EX)) {
            return Result.createResult(null, 0);
        }
        ToolTagQuery toolTagQuery = new ToolTagQuery(null, includeTags, excludeTags, pageNum, limit);
        return Result.createResult(this.toolService.getTools(toolTagQuery), 0);
    }

    /**
     * 查询 genericableId 下的所有 FitTool。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param genericableId 表示商品的泛服务标识的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param limit 表示分页查询的数量限制的 {@code int}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping(path = "/{platform}/fit/tool/genericables/{genericableId}")
    public Result<List<ToolData>> getFitTools(@PathVariable("platform") String platform,
            @PathVariable("genericableId") String genericableId, @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int limit) {
        notNegative(pageNum, "the page num cannot be negative. [pageNum={0}]", pageNum);
        notNegative(limit, "the page size cannot be negative. [pageSize={0}]", limit);
        if (this.decodeChinese(genericableId).equals(DECODE_EX)) {
            return Result.createResult(null, 0);
        }
        ToolTagQuery toolTagQuery =
                new ToolTagQuery(null, new ArrayList<>(Collections.singleton("FIT")), null, pageNum, limit);
        return Result.createResult(this.toolService.getTools(toolTagQuery), 0);
    }

    /**
     * 基于商品的唯一标识查询某个商品。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param uniqueName 表示商品的唯一索引的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link ToolData}{@code >}。
     */
    @GetMapping(path = "/{platform}")
    public Result<ToolData> getItemByUniqueName(@PathVariable("platform") String platform,
            @RequestParam("uniqueName") String uniqueName) {
        notBlank(uniqueName, "the tool unique name cannot be blank");
        return Result.createResult(this.toolService.getTool(uniqueName), 0);
    }

    /**
     * 删除商品。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param category 表示商品分类的 {@link String}。
     * @param uniqueName 表示商品的唯一索引的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @DeleteMapping(path = "/{platform}/categories/{category}")
    public Result<String> deleteItem(@PathVariable("platform") String platform,
            @PathVariable("category") String category, @RequestParam("uniqueName") String uniqueName) {
        return Result.createResult(this.toolService.deleteTool(uniqueName), 0);
    }

    /**
     * 根据商品分类、分组、名字和标签查询某个商品。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param category 表示商品分类的 {@link String}。
     * @param genericableId 表示商品的泛服务标识的 {@link String}。
     * @param itemName 表示商品名字的 {@link String}。
     * @param includeTags 表示要包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示不包括的标签的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link ToolData}{@code >}{@code >}。
     */
    @GetMapping(path = "/{platform}/categories/{category}/groups/{genericableId}/names/{itemName}")
    public Result<List<ToolData>> getItem(@PathVariable("platform") String platform,
            @PathVariable("category") String category, @PathVariable("genericableId") String genericableId,
            @PathVariable("itemName") String itemName, @RequestParam("includeTags") List<String> includeTags,
            @RequestParam("excludeTags") List<String> excludeTags) {
        if (this.decodeChinese(genericableId).equals(DECODE_EX) || this.decodeChinese(itemName).equals(DECODE_EX)) {
            return Result.createResult(null, 0);
        }
        ToolTagQuery toolTagQuery =
                new ToolTagQuery(this.decodeChinese(itemName), includeTags, excludeTags, null, null);
        return Result.createResult(this.toolService.getTools(toolTagQuery), 0);
    }

    private Map<String, Object> setFitRunnable(String genericableId, String fitableId) {
        Map<String, Object> fit = new HashMap<>();
        fit.put("genericableId", genericableId);
        fit.put("fitableId", fitableId);
        return fit;
    }

    private ToolData convertToolData(String name, Map<String, Object> schema, Map<String, Object> runnables,
            Set<String> tags) {
        ToolData toolData = new ToolData();
        toolData.setName(name);
        toolData.setSchema(schema);
        toolData.setRunnables(runnables);
        toolData.setDescription(schema.containsKey("description") ? schema.get("description").toString() : null);
        toolData.setTags(tags);
        return toolData;
    }

    private String decodeChinese(String input) {
        String res;
        try {
            res = URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return DECODE_EX;
        }
        return res;
    }
}
