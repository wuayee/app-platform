/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.controller;

import com.huawei.fit.http.annotation.DeleteMapping;
import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.store.service.DefaultFitToolService;
import com.huawei.jade.store.service.ItemData;
import com.huawei.jade.store.service.ItemService;
import com.huawei.jade.store.support.ItemRepositoryUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 处理用户通过 HTTP 协议发送的请求的控制器。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-16
 */
@Component
public class ItemController {
    private static final Logger log = Logger.get(ItemController.class);

    private final ItemService itemService;
    private final DefaultFitToolService fitToolService;

    /**
     * 通过商品通用服务和 FitTool 服务来初始化 {@link ItemController} 的新实例。
     *
     * @param itemService 表示商品通用服务的 {@link ItemService}。
     * @param fitToolService 表示 FitTool 服务的 {@link DefaultFitToolService}。
     */
    public ItemController(ItemService itemService, DefaultFitToolService fitToolService) {
        this.itemService = itemService;
        this.fitToolService = fitToolService;
    }

    /**
     * 添加 FitTool 的 Http Post 请求。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param toolName 表示 FitTool 名字的 {@link String}。
     * @param genericableId 表示分组的 {@link String}。
     * @param fitableId 表示泛服务实现的 {@link String}。
     * @param schema 表示 FitTool 格式的 {@link String}。
     * @return 格式化之后的返回消息的 {@link String}。
     */
    @PostMapping(path = "/store/platform/{platform}/fit/tool/names/{toolName}")
    public String addFitTool(@PathVariable("platform") String platform, @PathVariable("toolName") String toolName,
            @RequestParam("genericableId") String genericableId, @RequestParam("fitableId") String fitableId,
            @RequestBody("schema") Map<String, Object> schema) {
        String toolRealName = toolName;
        try {
            toolRealName = URLDecoder.decode(toolRealName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "error";
        }
        String group = genericableId + "#" + fitableId;
        ItemData item = new ItemData().setCategory("TOOL")
                .setGroup(group)
                .setName(toolRealName)
                .setSchema(schema)
                .setTags(Collections.singleton("FIT"));
        String res = this.fitToolService.addFitTool(item);
        if (Objects.equals(platform, "tianzhou") || Objects.equals(platform, "jade")) {
            return ItemRepositoryUtil.convertToJson(format(Arrays.asList(res)));
        }
        return res;
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
     * @return 格式化之后的返回消息的 {@link String}。
     */
    @PostMapping(path = "/store/platform/{platform}/categories/{category}/groups/{genericableId}/names/{itemName}")
    public String addItem(@PathVariable("platform") String platform, @PathVariable("category") String category,
            @PathVariable("genericableId") String genericableId, @PathVariable("itemName") String itemName,
            @RequestParam("tags") List<String> tags, @RequestParam("schema") String schema) {
        String group = genericableId;
        Map<String, Object> schemaObject = ItemRepositoryUtil.json2Obj(schema);
        ItemData item = new ItemData().setCategory(category)
                .setGroup(group)
                .setName(itemName)
                .setSchema(schemaObject)
                .setTags(new HashSet<>(tags));
        String res = this.itemService.addItem(item);
        if (Objects.equals(platform, "tianzhou") || Objects.equals(platform, "jade")) {
            return ItemRepositoryUtil.convertToJson(format(Arrays.asList(res)));
        }
        return res;
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
     * @return 格式化之后的返回消息的 {@link String}。
     */
    @GetMapping(path = "/store/platform/{platform}/categories/{category}")
    public String getAllItems(@PathVariable("platform") String platform, @PathVariable("category") String category,
            @RequestParam("includeTags") List<String> includeTags,
            @RequestParam("excludeTags") List<String> excludeTags, @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int limit) {
        List<ItemData> res = this.itemService.getAllItems(category, includeTags, excludeTags, pageNum, limit);
        if (Objects.equals(platform, "tianzhou") || Objects.equals(platform, "jade")) {
            return ItemRepositoryUtil.convertToJson(format(res));
        }
        return ItemRepositoryUtil.convertToJson(res);
    }

    /**
     * 查询所有的 genericables。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param limit 表示分页查询数量限制的 {@code int}。
     * @return 格式化之后的返回消息的 {@link String}。
     */
    @GetMapping(path = "/store/platform/{platform}/fit/tool/genericables")
    public String getAllGenericableIds(@PathVariable("platform") String platform, @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int limit) {
        List<String> res = this.fitToolService.getAllGenericableIds(pageNum, limit);
        if (Objects.equals(platform, "tianzhou") || Objects.equals(platform, "jade")) {
            return ItemRepositoryUtil.convertToJson(Arrays.asList(res));
        }
        return res.toString();
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
     * @return 格式化之后的返回消息的 {@link String}。
     */
    @GetMapping(path = "/store/platform/{platform}/categories/{category}/groups/{genericableId}")
    public String getItemsByGroup(@PathVariable("platform") String platform, @PathVariable("category") String category,
            @PathVariable("genericableId") String genericableId, @RequestParam("includeTags") List<String> includeTags,
            @RequestParam("excludeTags") List<String> excludeTags, @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int limit) {
        List<ItemData> res =
                this.itemService.getItems(category, genericableId, includeTags, excludeTags, pageNum, limit);
        if (Objects.equals(platform, "tianzhou") || Objects.equals(platform, "jade")) {
            return ItemRepositoryUtil.convertToJson(format(res));
        }
        return ItemRepositoryUtil.convertToJson(res);
    }

    /**
     * 查询 genericableId 下的所有 FitTool。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param genericableId 表示商品的泛服务标识的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param limit 表示分页查询的数量限制的 {@code int}。
     * @return 格式化之后的返回消息的 {@link String}。
     */
    @GetMapping(path = "/store/platform/{platform}/fit/tool/genericables/{genericableId}")
    public String getFitTools(@PathVariable("platform") String platform,
            @PathVariable("genericableId") String genericableId, @RequestParam("pageNum") int pageNum,
            @RequestParam("pageSize") int limit) {
        List<ItemData> res = this.fitToolService.getFitTools(genericableId, pageNum, limit);
        if (Objects.equals(platform, "tianzhou") || Objects.equals(platform, "jade")) {
            return ItemRepositoryUtil.convertToJson(format(res));
        }
        return ItemRepositoryUtil.convertToJson(res);
    }

    /**
     * 基于商品的唯一标识查询某个商品。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param uniqueName 表示商品的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link String}。
     */
    @GetMapping(path = "/store/platform/{platform}")
    public String getItemByUniqueName(@PathVariable("platform") String platform,
            @RequestParam("uniqueName") String uniqueName) {
        ItemData res = this.itemService.getItem(uniqueName);
        if (Objects.equals(platform, "tianzhou") || Objects.equals(platform, "jade")) {
            return ItemRepositoryUtil.convertToJson(format(Arrays.asList(res)));
        }
        return ItemRepositoryUtil.convertToJson(res);
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
     * @return 格式化之后的返回消息的 {@link String}。
     */
    @GetMapping(path = "/store/platform/{platform}/categories/{category}/groups/{genericableId}/names/{itemName}")
    public String getItem(@PathVariable("platform") String platform, @PathVariable("category") String category,
            @PathVariable("genericableId") String genericableId, @PathVariable("itemName") String itemName,
            @RequestParam("includeTags") List<String> includeTags,
            @RequestParam("excludeTags") List<String> excludeTags) {
        ItemData res = this.itemService.getItem(category, genericableId, itemName, includeTags, excludeTags);
        if (Objects.equals(platform, "tianzhou") || Objects.equals(platform, "jade")) {
            return ItemRepositoryUtil.convertToJson(format(Arrays.asList(res)));
        }
        return ItemRepositoryUtil.convertToJson(res);
    }

    /**
     * 删除商品。
     *
     * @param platform 表示发送请求的平台的 {@link String}。
     * @param category 表示商品分类的 {@link String}。
     * @param uniqueName 表示商品的唯一索引的 {@link String}。
     */
    @DeleteMapping(path = "/store/platform/{platform}/categories/{category}")
    public void deleteItem(@PathVariable("platform") String platform, @PathVariable("category") String category,
            @RequestParam("uniqueName") String uniqueName) {
        this.itemService.deleteItem(uniqueName);
    }

    private <T> Map<String, Object> format(List<T> input) {
        Map<String, Object> mp = new HashMap<>();
        mp.put("data", input);
        mp.put("code", 0);
        mp.put("total", input.size());
        return mp;
    }
}