/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.service;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.store.Item;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.repository.ItemRepository;
import com.huawei.jade.store.support.ItemRepositoryUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 商品的增删查改服务的 MyBatis 实现。
 *
 * @author 鲁为 l00839724
 * @since 2024-04-16
 */
@Component
public class DefaultItemService implements ItemService {
    private static final Logger log = Logger.get(DefaultItemService.class);

    private final ItemRepository repo;

    /**
     * 通过持久层接口来初始化 {@link DefaultItemService} 的实例。
     *
     * @param repo 表示持久层实例的 {@link ItemRepository}。
     */
    public DefaultItemService(ItemRepository repo) {
        this.repo = repo;
    }

    @Override
    @Fitable("addItemTag")
    public void addItemTag(String itemUniqueName, String tag) {
        this.repo.addItemTag(itemUniqueName, tag);
    }

    @Override
    @Fitable("deleteItemTag")
    public void deleteItemTag(String itemUniqueName, String tag) {
        this.repo.deleteItemTag(itemUniqueName, tag);
    }

    @Override
    @Fitable("addItem")
    public String addItem(ItemData item) {
        item.setUniqueName(UUID.randomUUID().toString());
        ItemInfo itemInfo = ItemData.convertToItemInfo(item);
        return this.repo.addItem(itemInfo);
    }

    @Override
    @Fitable("deleteItem")
    public void deleteItem(String itemUniqueName) {
        this.repo.deleteItem(itemUniqueName);
    }

    @Override
    @Fitable("getItemByUniqueName")
    public ItemData getItem(String itemUniqueName) {
        Optional<Item> item = this.repo.getItem(itemUniqueName);
        if (!item.isPresent()) {
            return null;
        }
        return ItemData.from(item.get().itemInfo());
    }

    @Override
    @Fitable("getAllItems")
    public List<ItemData> getAllItems(String category, List<String> includeTags, List<String> excludeTags, int pageNum,
            int limit) {
        return ItemData.fromItems(this.repo.getAllItems(category, includeTags, excludeTags,
                ItemRepositoryUtil.getOffset(pageNum, limit), limit));
    }

    @Override
    @Fitable("getItemByCategoryGroupNameTags")
    public ItemData getItem(String category, String group, String name, List<String> includeTags,
            List<String> excludeTags) {
        Optional<Item> item = this.repo.getItem(category, group, name, includeTags, excludeTags);
        if (!item.isPresent()) {
            return null;
        }
        return ItemData.from(item.get().itemInfo());
    }

    @Override
    @Fitable("getItemsByCategoryGroupTags")
    public List<ItemData> getItems(String category, String group, List<String> includeTags, List<String> excludeTags,
            int pageNum, int limit) {
        return ItemData.fromItems(this.repo.getItems(category, group, includeTags, excludeTags,
                ItemRepositoryUtil.getOffset(pageNum, limit), limit));
    }
}
