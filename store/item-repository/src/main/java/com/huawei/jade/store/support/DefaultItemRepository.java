/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.jade.store.Application;
import com.huawei.jade.store.Item;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.ToolFactory;
import com.huawei.jade.store.mybatis.ItemDo;
import com.huawei.jade.store.mybatis.ItemMapper;
import com.huawei.jade.store.mybatis.Tag;
import com.huawei.jade.store.repository.ItemRepository;
import com.huawei.jade.store.repository.ToolFactoryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 存入数据库的工具的实体类。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
@Component
public class DefaultItemRepository implements ItemRepository {
    private static final Logger log = Logger.get(DefaultItemRepository.class);
    private static final String CATEGORY_TYPE_APP = "APP";

    private final ToolFactoryRepository toolFactoryRepository;

    private final ItemMapper repo;

    /**
     * 通过持久层接口来初始化 {@link DefaultItemRepository} 的实例。
     *
     * @param repo 表示持久层实例的 {@link ItemMapper}。
     * @param toolFactoryRepository 表示工具工厂的实例的 {@link ToolFactoryRepository}。
     */
    public DefaultItemRepository(ItemMapper repo, ToolFactoryRepository toolFactoryRepository) {
        this.repo = notNull(repo, "The repo cannot be null.");
        this.toolFactoryRepository = notNull(toolFactoryRepository, "The tool factory repo cannot be null.");
    }

    @Override
    public void addItemTag(String itemUniqueName, String tag) {
        ItemDo res = this.repo.getItem(itemUniqueName);
        Tag tag1 = new Tag();
        tag1.setTag(tag);
        tag1.setItemId(res.getId());
        this.repo.addTag(tag1);
    }

    @Override
    public void deleteItemTag(String itemUniqueName, String tag) {
        ItemDo res = this.repo.getItem(itemUniqueName);
        this.repo.deleteTag(res.getId(), tag);
    }

    @Override
    public String addItem(ItemInfo itemInfo) {
        ItemDo itemDo = ItemDo.from(itemInfo);
        this.repo.addItem(itemDo);
        ItemDo res = this.repo.getItem(itemInfo.uniqueName());

        for (String tag : itemInfo.tags()) {
            Tag toAddTag = new Tag();
            toAddTag.setCreatedTime(res.getCreatedTime());
            toAddTag.setUpdatedTime(res.getUpdatedTime());
            toAddTag.setCreator(res.getCreator());
            toAddTag.setModifier(res.getModifier());
            toAddTag.setTag(tag);
            toAddTag.setItemId(res.getId());
            this.repo.addTag(toAddTag);
        }
        return itemInfo.uniqueName();
    }

    @Override
    public void deleteItem(String itemUniqueName) {
        this.repo.deleteItem(itemUniqueName);
    }

    @Override
    public Optional<Item> getItem(String itemUniqueName) {
        ItemDo dObject = this.repo.getItemByUniqueName(itemUniqueName);
        List<Tag> tagObjs = this.repo.getTags(dObject.getUniqueName());

        return this.create(convertToItemInfo(dObject, tagObjs));
    }

    @Override
    public List<Item> getAllItems(String category, List<String> includeTags, List<String> excludeTags, int offset,
            int limit) {
        Map<String, Object> mp = new BuildParameter().category(category)
                .includeTags(includeTags)
                .excludeTags(excludeTags)
                .offset(offset)
                .limit(limit)
                .build();
        return convertToItems(this.repo.getAllItems(mp));
    }

    @Override
    public Optional<Item> getItem(String category, String group, String name, List<String> includeTags,
            List<String> excludeTags) {
        Map<String, Object> mp = new BuildParameter().category(category)
                .group(group)
                .name(name)
                .includeTags(includeTags)
                .excludeTags(excludeTags)
                .build();
        ItemDo dObject = this.repo.getItemByCategoryGroupNameTags(mp);
        List<Tag> tagObjs = this.repo.getTags(dObject.getUniqueName());
        return this.create(convertToItemInfo(dObject, tagObjs));
    }

    @Override
    public List<Item> getItems(String category, String group, List<String> includeTags, List<String> excludeTags,
            int offset, int limit) {
        Map<String, Object> mp = new BuildParameter().category(category)
                .group(group)
                .includeTags(includeTags)
                .excludeTags(excludeTags)
                .offset(offset)
                .limit(limit)
                .build();
        return convertToItems(this.repo.getItemsByGroup(mp));
    }

    @Override
    public List<String> getAllGroups(String category, String tag, int offset, int limit) {
        return this.repo.getAllGenericableIds("TOOL", "FIT", offset, limit);
    }

    @Override
    public List<Item> getItemsByGroup(String category, String tag, String group, int offset, int limit) {
        return convertToItems(this.repo.getFitTools(category, tag, group, offset, limit));
    }

    private List<Item> convertToItems(List<ItemDo> dos) {
        List<Item> items = new ArrayList<>();
        for (ItemDo dObject : dos) {
            List<Tag> tagObjs = this.repo.getTags(dObject.getUniqueName());
            Optional<Item> item = this.create(convertToItemInfo(dObject, tagObjs));
            if (item.isPresent()) {
                items.add(item.get());
            }
        }
        return items;
    }

    private Optional<Item> create(ItemInfo itemInfo) {
        if (CATEGORY_TYPE_APP.equals(itemInfo.category())) {
            return Optional.of(Application.create(itemInfo));
        }
        Optional<ToolFactory> toolFactory = this.toolFactoryRepository.query(itemInfo.tags());
        if (!toolFactory.isPresent()) {
            log.error("category is: {0}, tags are: {1}", itemInfo.category(), itemInfo.tags().toString());
            return Optional.empty();
        }
        return Optional.ofNullable(toolFactory.get().create(itemInfo, Tool.Metadata.fromSchema(itemInfo.schema())));
    }

    /**
     * 将数据库层数据对象转换成领域对象。
     *
     * @param itemDo 表示数据库层数据对象的 {@link ItemDo}。
     * @param tags 表示商品的标签的 {@link List}{@code <}{@link Tag}{@code >}。
     * @return 领域对象的 {@link ItemInfo}。
     */
    public static ItemInfo convertToItemInfo(ItemDo itemDo, List<Tag> tags) {
        Map<String, Object> schema = ItemRepositoryUtil.json2Obj(itemDo.getSchema());
        String desc = "";
        Object descr = schema.get("description");
        if (descr instanceof String) {
            desc = (String) descr;
        }
        return ItemInfo.custom()
                .category(itemDo.getCategory())
                .group(itemDo.getGroup())
                .name(itemDo.getName())
                .uniqueName(itemDo.getUniqueName())
                .tags(tags.stream().map(Tag::getTag).collect(Collectors.toSet()))
                .description(desc)
                .schema(schema)
                .build();
    }

    class BuildParameter {
        private final Map<String, Object> mp = new HashMap<>();

        /**
         * 构造分类参数。
         *
         * @param category 表示待构造的分类的 {@link String}。
         * @return 参数的 {@link BuildParameter}。
         */
        public BuildParameter category(String category) {
            mp.put("category", category);
            return this;
        }

        /**
         * 构造分组参数。
         *
         * @param group 表示待构造的分组的 {@link String}。
         * @return 参数的 {@link BuildParameter}。
         */
        public BuildParameter group(String group) {
            mp.put("group", group);
            return this;
        }

        /**
         * 构造名字参数。
         *
         * @param name 表示待构造的名字的 {@link String}。
         * @return 参数的 {@link BuildParameter}。
         */
        public BuildParameter name(String name) {
            mp.put("name", name);
            return this;
        }

        /**
         * 构造包含标签参数。
         *
         * @param includeTags 表示待构造的包含标签的 {@link List}{@code <}{@link String}{@code >}。
         * @return 参数的 {@link BuildParameter}。
         */
        public BuildParameter includeTags(List<String> includeTags) {
            mp.put("includeTags", includeTags);
            return this;
        }

        /**
         * 构造去除标签参数。
         *
         * @param excludeTags 表示待构造的去除标签的 {@link List}{@code <}{@link String}{@code >}。
         * @return 参数的 {@link BuildParameter}。
         */
        public BuildParameter excludeTags(List<String> excludeTags) {
            mp.put("excludeTags", excludeTags);
            return this;
        }

        /**
         * 构造偏移量参数。
         *
         * @param offset 表示待构造的偏移量的 {@link String}。
         * @return 参数的 {@link BuildParameter}。
         */
        public BuildParameter offset(int offset) {
            mp.put("offset", offset);
            return this;
        }

        /**
         * 构造限制数量参数。
         *
         * @param limit 表示待构造的限制数量的 {@link String}。
         * @return 参数的 {@link BuildParameter}。
         */
        public BuildParameter limit(int limit) {
            mp.put("limit", limit);
            return this;
        }

        /**
         * 构造方法。
         *
         * @return 商品的格式的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
         */
        public Map<String, Object> build() {
            return mp;
        }
    }
}
