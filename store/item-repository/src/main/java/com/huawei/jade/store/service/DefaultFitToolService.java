/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.repository.ItemRepository;
import com.huawei.jade.store.support.ItemRepositoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * FitTool 的增删查改服务的 MyBatis 实现。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-26
 */
@Component
public class DefaultFitToolService implements FitToolService {
    private static final Logger log = Logger.get(DefaultItemService.class);

    private final ItemRepository repo;

    /**
     * 通过持久层接口来初始化 {@link DefaultItemService} 的实例。
     *
     * @param repo 表示持久层实例的 {@link ItemRepository}。
     */
    public DefaultFitToolService(ItemRepository repo) {
        this.repo = repo;
    }

    @Override
    @Fitable("addFitTool")
    public String addFitTool(ItemData item) {
        if (!item.getGroup().contains("#")) {
            item.setGroup(item.getGroup() + "#" + item.getSchema().get("fitableId"));
        }

        item.setUniqueName(UUID.randomUUID().toString());
        ItemInfo itemInfo = ItemData.convertToItemInfo(item);
        return this.repo.addItem(itemInfo);
    }

    @Override
    @Fitable("getAllGenericableIds")
    public List<String> getAllGenericableIds(int pageNum, int limit) {
        List<String> groups = this.repo.getAllGroups("TOOL", "FIT",
                ItemRepositoryUtil.getOffset(pageNum, limit), limit);
        List<String> res = new ArrayList<>();
        for (String group : groups) {
            res.add(group.split("#")[0]);
        }
        return res.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());
    }

    @Override
    @Fitable("getFitTools")
    public List<ItemData> getFitTools(String genericableId, int pageNum, int limit) {
        return ItemData.fromItems(this.repo.getItemsByGroup("TOOL", "FIT",
                genericableId, ItemRepositoryUtil.getOffset(pageNum, limit), limit));
    }
}
